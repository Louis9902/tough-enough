package io.github.minnecraeft.toughenough.client.item;

import io.github.minnecraeft.toughenough.api.temperature.TemperatureConstants;
import io.github.minnecraeft.toughenough.api.temperature.TemperatureModifiers;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

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
            int target = TemperatureModifiers.calcTargetForBlock(clientWorld, pos);
            //Scale from [MIN_TARGET;MAX_TARGET] to [0;MIN_TARGET+MAX_TARGET] to [0;1]
            return (target - TemperatureConstants.MIN_TEMPERATURE_TARGET) / ((float) TemperatureConstants.MAX_TEMPERATURE_TARGET - TemperatureConstants.MIN_TEMPERATURE_TARGET);
        }
    }
}
