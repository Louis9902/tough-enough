package io.github.louis9902.toughenough.stats;

import io.github.louis9902.toughenough.item.Drinkable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class ThirstManager {

    public static final int MAX_THIRST_LEVEL = 20;

    private int prevThirstLevel;
    /**
     * The amount of
     */
    private int thirst;
    /**
     * This value determines how long a player can sustain the
     * current exhaustion before the thirst is used.
     */
    private float hydration;
    private float exhaustion;

    private int thirstTimer;

    public ThirstManager() {
        thirst = 20;
        hydration = 5.0f;
    }

    public void add(int thirst, float hydration) {
        this.thirst = Math.min(MAX_THIRST_LEVEL, this.thirst + thirst);
        this.hydration = Math.min((float) this.thirst, this.hydration + (thirst * hydration * 2.0F));
    }

    public void drink(ItemStack stack) {
        Drinkable drinkable = stack.getItem() instanceof Drinkable ? ((Drinkable) stack.getItem()) : null;
        if (drinkable != null) {
            this.add(drinkable.getThirst(), drinkable.getHydrationModifier());
        }
    }

    public void update(PlayerEntity player) {

    }

    public void fromTag(CompoundTag compound) {
        if (compound.contains("thirst", 99)) {
            this.thirst = compound.getInt("thirstLevel");
            this.hydration = compound.getInt("thirstHydrationLevel");
            this.exhaustion = compound.getFloat("thirstExhaustionLevel");
        }
    }

    public void toTag(CompoundTag compound) {
        compound.putInt("thirstLevel", this.thirst);
        compound.putFloat("thirstHydrationLevel", this.hydration);
        compound.putFloat("thirstExhaustionLevel", this.exhaustion);
    }

    public void setThirst(int thirst) {
        this.thirst = thirst;
    }

    public void setHydration(float hydration) {
        this.hydration = hydration;
    }

    public void setExhaustion(float exhaustion) {
        this.exhaustion = exhaustion;
    }
}
