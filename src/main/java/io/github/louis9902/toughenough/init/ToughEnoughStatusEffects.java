package io.github.louis9902.toughenough.init;

import io.github.louis9902.toughenough.ToughEnough;
import io.github.louis9902.toughenough.entity.effect.HyperthermiaStatusEffect;
import io.github.louis9902.toughenough.entity.effect.HypothermiaStatusEffect;
import io.github.louis9902.toughenough.entity.effect.ThirstStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.registry.Registry;

import static net.minecraft.util.registry.Registry.STATUS_EFFECT;

public final class ToughEnoughStatusEffects {

    public static final StatusEffect HYPERTHERMIA;
    public static final StatusEffect HYPOTHERMIA;
    public static final StatusEffect THIRST;

    static {
        HYPERTHERMIA = register("hyperthermia", new HyperthermiaStatusEffect());
        HYPOTHERMIA = register("hypothermia", new HypothermiaStatusEffect());
        THIRST = register("thirst", new ThirstStatusEffect());
    }

    private static <T extends StatusEffect> T register(String name, T effect) {
        return Registry.register(STATUS_EFFECT, ToughEnough.identifier(name), effect);
    }

    public static void register() {
        // keep for class initialisation (call from initializer)
    }

}
