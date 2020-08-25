package io.github.louis9902.toughenough.components.defaults;

import io.github.louis9902.toughenough.components.Drink;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

public class DefaultDrink implements Drink {

    private int thirst;
    private float hydration;

    public DefaultDrink(int thirst, float hydration) {
        this.thirst = thirst;
        this.hydration = hydration;
    }

    @Override
    public int getThirst() {
        return thirst;
    }

    @Override
    public float getHydration() {
        return hydration;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        thirst = tag.getInt("thirst");
        hydration = tag.getFloat("hydration");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt("thirst", thirst);
        tag.putFloat("hydration", hydration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultDrink)) return false;
        DefaultDrink that = (DefaultDrink) o;
        return getThirst() == that.getThirst() &&
                Float.compare(that.getHydration(), getHydration()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(thirst, hydration);
    }

    @Override
    public String toString() {
        return "DefaultDrink{thirst=" + getThirst() + ", hydration=" + getHydration() + '}';
    }
}
