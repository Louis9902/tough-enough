package io.github.louis9902.toughenough.item.drink;

import io.github.louis9902.toughenough.components.Drink;

public enum JuiceType implements Drink.Modifiers {

    APPLE(5, .8f, 254, 181, 0),
    BEETROOT(5, .8f, 81, 17, 15),
    CACTUS(5, .8f, 189, 214, 156),
    SWEET_BERRY(5, .8f, 176, 46, 38);

    private final int thirst;
    private final float hydration;
    public final int color;

    JuiceType(int thirst, float hydration, int r, int g, int b) {
        this.thirst = thirst;
        this.hydration = hydration;
        this.color = r << 16 | g << 8 | b;
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
