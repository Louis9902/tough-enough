package io.github.minnecraeft.toughenough.thirst.drinks;

import io.github.minnecraeft.toughenough.api.thirst.Drink;

public enum WaterType implements Drink.Modifiers {

    NORMAL(3, 0.1F, 0.75F),
    PURIFY(6, 0.5F, 0.0F);

    private final int thirst;
    private final float hydration;
    private final float poisonChance;

    WaterType(int thirst, float hydration, float poisonChance) {
        this.thirst = thirst;
        this.hydration = hydration;
        this.poisonChance = poisonChance;
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
    public float getPoisonChance() {
        return poisonChance;
    }
}
