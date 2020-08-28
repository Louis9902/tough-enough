package io.github.louis9902.toughenough.api.temperature;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class TemperatureModifier {

    private final Identifier identifier;

    public TemperatureModifier(Identifier id) {
        this.identifier = id;
    }

    public int applyTargetFromPlayer(@NotNull PlayerEntity player) {
        return applyTargetFromEnvironment(player.world, player.getBlockPos());
    }

    public int applyTargetFromEnvironment(@NotNull World world, @NotNull BlockPos pos) {
        return 0;
    }

    public int applyRateFromPlayer(@NotNull PlayerEntity player) {
        return applyRateFromEnvironment(player.world, player.getBlockPos());
    }

    public int applyRateFromEnvironment(@NotNull World world, @NotNull BlockPos pos) {
        return 0;
    }

    public abstract boolean isPlayerModifier();

    public @NotNull Identifier getIdentifier() {
        return identifier;
    }

}
