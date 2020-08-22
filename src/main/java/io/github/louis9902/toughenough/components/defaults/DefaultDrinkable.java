package io.github.louis9902.toughenough.components.defaults;

import io.github.louis9902.toughenough.ToughEnoughComponents;
import io.github.louis9902.toughenough.components.Drinkable;
import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

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
    public void fromTag(CompoundTag compoundTag) {
        thirst = compoundTag.getInt("thirst");
        hydrationModifier = compoundTag.getFloat("hydration");
    }

    @Override
    public @NotNull CompoundTag toTag(CompoundTag compoundTag) {
        compoundTag.putInt("thirst", thirst);
        compoundTag.putFloat("hydration", hydrationModifier);
        return compoundTag;
    }

    @Override
    public @NotNull ComponentType<?> getComponentType() {
        return ToughEnoughComponents.DRINKABLE;
    }
}
