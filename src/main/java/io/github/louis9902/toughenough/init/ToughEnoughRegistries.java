package io.github.louis9902.toughenough.init;

import io.github.louis9902.toughenough.ToughEnough;
import io.github.louis9902.toughenough.api.thirst.Drink;
import io.github.louis9902.toughenough.item.drink.WaterType;
import net.minecraft.util.registry.Registry;

import static net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder.createSimple;

public final class ToughEnoughRegistries {

    public static final Registry<Drink.Modifiers> DRINK_MODIFIERS;

    static {
        DRINK_MODIFIERS = createSimple(Drink.Modifiers.class, ToughEnough.identifier("drink_modifier")).buildAndRegister();
    }

    public static void register() {
        registerDrinkModifiers();
    }

    private static void registerDrinkModifiers() {
        Registry.register(DRINK_MODIFIERS, ToughEnough.identifier("water_normal"), WaterType.NORMAL);
        Registry.register(DRINK_MODIFIERS, ToughEnough.identifier("water_purify"), WaterType.PURIFY);
    }

}
