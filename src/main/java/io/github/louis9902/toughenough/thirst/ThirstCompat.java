package io.github.louis9902.toughenough.thirst;

import io.github.louis9902.toughenough.api.thirst.Drink;
import io.github.louis9902.toughenough.components.DefaultDrink;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class ThirstCompat {

    public static final Map<Identifier, Drink> DRINKS = new HashMap<>();

    static {
        DRINKS.put(new Identifier("minecraft:honey_bottle"), new DefaultDrink(false, new SimpleModifiers(4, 0.1f)));
        DRINKS.put(new Identifier("minecraft:milk_bucket"), new DefaultDrink(false, new SimpleModifiers(6, 0.3f)));
        DRINKS.put(new Identifier("minecraft:potion"), new DefaultDrink(false, new SimpleModifiers(4, 0.5f)));

        SimpleModifiers stew = new SimpleModifiers(8, 0.7f);
        DRINKS.put(new Identifier("minecraft:mushroom_stew"), new DefaultDrink(false, stew));
        DRINKS.put(new Identifier("minecraft:rabbit_stew"), new DefaultDrink(false, stew));
        DRINKS.put(new Identifier("minecraft:suspicious_stew"), new DefaultDrink(false, stew));
    }

    private static class SimpleModifiers implements Drink.Modifiers {

        private final int thirst;
        private final float hydration;

        public SimpleModifiers(int thirst, float hydration) {
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
        public float getPoisonChance() {
            return 0;
        }
    }

}
