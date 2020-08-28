package io.github.louis9902.toughenough.temperature.api;

import io.github.louis9902.toughenough.misc.DebugMonitor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TemperatureModifier {
    default int applyTargetFromEnvironment(@NotNull World world, @NotNull BlockPos pos, @Nullable DebugMonitor monitor) {
        return 0;
    }

    default int applyTargetFromPlayer(@NotNull PlayerEntity playerEntity, @Nullable DebugMonitor monitor) {
        return applyTargetFromEnvironment(playerEntity.world, playerEntity.getBlockPos(), monitor);
    }

    default int applyRateFromEnvironment(@NotNull World world, @NotNull BlockPos pos, @Nullable DebugMonitor monitor) {
        return 0;
    }

    default int applyRateFromPlayer(@NotNull PlayerEntity playerEntity, @Nullable DebugMonitor monitor) {
        return applyRateFromEnvironment(playerEntity.world, playerEntity.getBlockPos(), monitor);
    }

    boolean isPlayerModifier();

    @NotNull Identifier getId();
}
