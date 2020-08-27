package io.github.louis9902.toughenough.temperature.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public interface TemperatureModifier {
    default int applyTargetFromEnvironment(@NotNull World world, @NotNull BlockPos pos) {
        return 0;
    }

    default int applyTargetFromPlayer(@NotNull PlayerEntity playerEntity) {
        return applyTargetFromEnvironment(playerEntity.world, playerEntity.getBlockPos());
    }

    default int applyRateFromEnvironment(@NotNull World world, @NotNull BlockPos pos) {
        return 0;
    }

    default int applyRateFromPlayer(@NotNull PlayerEntity playerEntity) {
        return applyRateFromEnvironment(playerEntity.world, playerEntity.getBlockPos());
    }

    boolean isPlayerModifier();

    @NotNull Identifier getId();
}
