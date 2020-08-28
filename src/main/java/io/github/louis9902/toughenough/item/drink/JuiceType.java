package io.github.louis9902.toughenough.item.drink;

import io.github.louis9902.toughenough.api.thirst.Drink;

public enum JuiceType implements Drink.Modifiers {

    APPLE(5, .8f, 254, 181, 0),
    BEETROOT(5, .8f, 81, 17, 15),
    CACTUS(5, .8f, 189, 214, 156),
    CARROT(0, 0, 241, 120, 23),
    MELON(0, 0, 255, 150, 128),
    PUMPKIN(0, 0, 233, 103, 39),
    SWEET_BERRY(5, .8f, 176, 46, 38);

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
