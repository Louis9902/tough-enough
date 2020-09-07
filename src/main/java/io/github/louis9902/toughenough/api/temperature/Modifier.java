package io.github.louis9902.toughenough.api.temperature;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class Modifier {

    private final Identifier identifier;

    protected Modifier(@NotNull Identifier identifier) {
        this.identifier = identifier;
    }

    public @NotNull Identifier getIdentifier() {
        return identifier;
    }

    public abstract boolean isPlayerModifier();

    public int calculateFromPlayer(@NotNull PlayerEntity player) {
        return calculateFromEnvironment(player.world, player.getBlockPos());
    }

    public int calculateFromEnvironment(@NotNull World world, @NotNull BlockPos pos) {
        return 0;
    }

    public static class Temporary {

        private Identifier identifier;
        private int amount;
        private int duration;

        public Temporary(@NotNull Identifier identifier, int amount, int duration) {
            this.identifier = identifier;
            this.amount = amount;
            this.duration = duration;
        }

        public Temporary(@NotNull CompoundTag compound) {
            decode(compound);
        }

        public static String asIdentifier(Temporary temporary) {
            return temporary.getIdentifier().toString();
        }

        public boolean update() {
            return --duration <= 0;
        }

        public @NotNull Identifier getIdentifier() {
            return identifier;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public void decode(CompoundTag compound) {
            identifier = new Identifier(compound.getString("Id"));
            setAmount(compound.getInt("Amount"));
            setDuration(compound.getInt("Duration"));
        }

        public CompoundTag encode() {
            CompoundTag compound = new CompoundTag();
            compound.putString("Id", getIdentifier().toString());
            compound.putInt("Amount", getAmount());
            compound.putInt("Duration", getDuration());
            return compound;
        }

    }

}
