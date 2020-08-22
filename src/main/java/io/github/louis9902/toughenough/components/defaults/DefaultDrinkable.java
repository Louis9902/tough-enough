package io.github.louis9902.toughenough.components.defaults;

import io.github.louis9902.toughenough.components.Drinkable;
import net.minecraft.nbt.CompoundTag;

public class DefaultDrinkable implements Drinkable {
    int thirst;
    float hydrationModifier;

    public DefaultDrinkable(int thirst, float hydrationModifier) {
        this.thirst = thirst;
        this.hydrationModifier = hydrationModifier;
    }

    @Override
    public int getThirst() {
        return thirst;
    }

    @Override
    public float getHydrationModifier() {
        return hydrationModifier;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        thirst = tag.getInt("thirst");
        hydrationModifier = tag.getFloat("hydration");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt("thirst", thirst);
        tag.putFloat("hydration", hydrationModifier);
    }

    //Item components should implement proper equals methods as they might be checked against
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultDrinkable that = (DefaultDrinkable) o;

        if (thirst != that.thirst) return false;
        return Float.compare(that.hydrationModifier, hydrationModifier) == 0;
    }
}
