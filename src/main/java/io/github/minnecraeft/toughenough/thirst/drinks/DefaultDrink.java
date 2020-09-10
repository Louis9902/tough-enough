package io.github.minnecraeft.toughenough.thirst.drinks;

import io.github.minnecraeft.toughenough.api.thirst.Drink;
import io.github.minnecraeft.toughenough.init.ToughEnoughRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DefaultDrink implements Drink {

    private final boolean persistent;
    private Modifiers modifiers;

    public DefaultDrink(boolean persistent, Modifiers modifiers) {
        this.modifiers = modifiers;
        this.persistent = persistent;
    }

    @Override
    public @NotNull Modifiers getModifiers() {
        return modifiers;
    }

    @Override
    public void setModifiers(@NotNull Modifiers modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public void readFromNbt(CompoundTag compound) {
        if (!persistent) return;
        if (compound.contains("modifiers", 8)) {
            String id = compound.getString("modifiers");
            modifiers = ToughEnoughRegistries.DRINK_MODIFIERS.get(new Identifier(id));
        }
    }

    @Override
    public void writeToNbt(CompoundTag compound) {
        if (!persistent) return;
        Identifier id = ToughEnoughRegistries.DRINK_MODIFIERS.getId(modifiers);
        if (id != null) {
            compound.putString("modifiers", id.toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultDrink)) return false;
        DefaultDrink that = (DefaultDrink) o;
        return persistent == that.persistent &&
                getModifiers().equals(that.getModifiers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(persistent, getModifiers());
    }

    @Override
    public String toString() {
        return "DefaultDrink{thirst=" + getThirst() + ", hydration=" + getHydration() + ", poisonChance=" + getPoisonChance() + '}';
    }
}
