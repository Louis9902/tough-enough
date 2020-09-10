package io.github.minnecraeft.toughenough.thirst.drinks;

import io.github.minnecraeft.toughenough.api.thirst.Drink;

public enum JuiceType implements Drink.Modifiers {

    APPLE(8, 0.8f, 254, 181, 0),
    BEETROOT(10, 0.8f, 81, 17, 15),
    CACTUS(6, 0.2f, 189, 214, 156),
    CARROT(8, 0.6f, 241, 120, 23),
    MELON(8, 0.5f, 255, 150, 128),
    PUMPKIN(7, 0.7f, 233, 103, 39),
    SWEET_BERRY(6, 0.7f, 176, 46, 38);

    private final int thirst;
    private final float hydration;
    public final int color;

    JuiceType(int thirst, float hydration, int r, int g, int b) {
        this.thirst = thirst;
        this.hydration = hydration;
        this.color = 255 << 24 | r << 16 | g << 8 | b;
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
        return 0.0f;
    }
}
