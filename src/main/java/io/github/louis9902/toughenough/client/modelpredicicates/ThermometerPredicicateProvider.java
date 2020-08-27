package io.github.louis9902.toughenough.client.modelpredicicates;

import io.github.louis9902.toughenough.temperature.TemperatureHelper;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import static io.github.louis9902.toughenough.temperature.HeatManagerConstants.MAX_TARGET;
import static io.github.louis9902.toughenough.temperature.HeatManagerConstants.MIN_TARGET;

public class ThermometerPredicicateProvider implements ModelPredicateProvider {
    // TODO don't calculate heat every frame -> MASSIVE performance gain
    private static final int UPDATE_INTERVAL = 60;

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
            int target = TemperatureHelper.calculateBlockTarget(clientWorld, pos);
            //Scale from [MIN_TARGET;MAX_TARGET] to [0;MIN_TARGET+MAX_TARGET] to [0;1]
            return (target - MIN_TARGET) / ((float) MAX_TARGET - MIN_TARGET);
        }
    }
}
