package io.github.louis9902.toughenough.client.item;

import io.github.louis9902.toughenough.temperature.TemperatureHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static io.github.louis9902.toughenough.temperature.HeatManagerConstants.MAX_TARGET;
import static io.github.louis9902.toughenough.temperature.HeatManagerConstants.MIN_TARGET;

public class ThermometerPredicateProvider implements ModelPredicateProvider {
    private static final int REFRESH_RATE = 5;
    //This map caches the calculations for the temperature at a specific block position,
    //without this the temperature is calculated every frame for every item.
    //In my performance testing this reduced the FPS drop of a double chest full of thermometers from
    //roughly 50% (250 fps -> 120) to negligible amounts (250 -> 245)
    private static final Map<BlockPos, Integer> cache = new HashMap<>();
    static int refreshCounter = 0;

    static {
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            if (refreshCounter++ >= REFRESH_RATE) {
                cache.clear();
                refreshCounter = 0;
            }
        });
    }

    @Override
    public float call(ItemStack stack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity) {
        Entity entity = livingEntity == null ? stack.getHolder() : livingEntity;
        if (entity == null) {
            return 0.0F;
        } else {
            if (clientWorld == null && entity.world instanceof ClientWorld)
                clientWorld = (ClientWorld) entity.world;

            if (clientWorld == null) {
                return 0.0F;
            }
            BlockPos pos = entity.getBlockPos();

            @Nullable final ClientWorld finalClientWorld = clientWorld;
            int target = cache.computeIfAbsent(pos, (p) -> TemperatureHelper.calcTargetForBlock(finalClientWorld, p, null));

            //Scale from [MIN_TARGET;MAX_TARGET] to [0;MIN_TARGET+MAX_TARGET] to [0;1]
            return (target - MIN_TARGET) / ((float) MAX_TARGET - MIN_TARGET);
        }
    }
}
