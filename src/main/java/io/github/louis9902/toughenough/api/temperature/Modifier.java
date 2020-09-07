package io.github.louis9902.toughenough.api.temperature;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class Modifier {

    private final Identifier identifier;

    protected Modifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public abstract boolean isPlayerModifier();

    public @NotNull Identifier getIdentifier() {
        return identifier;
    }

    public int calculateFromPlayer(@NotNull PlayerEntity player) {
        return calculateFromEnvironment(player.world, player.getBlockPos());
    }

    public int calculateFromEnvironment(@NotNull World world, @NotNull BlockPos pos) {
        return 0;
    }

}
