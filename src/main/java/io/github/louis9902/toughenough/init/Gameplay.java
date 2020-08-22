package io.github.louis9902.toughenough.init;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.Category;
import net.minecraft.world.World;

import static net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory.createBooleanRule;

public final class Gameplay {

    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_THIRST;

    static {
        ENABLE_THIRST = GameRuleRegistry.register("enableThirst", Category.PLAYER, createBooleanRule(true));
    }

    public static boolean isThirstEnabled(World world) {
        return world.getGameRules().getBoolean(ENABLE_THIRST);
    }

    public static void register() {
        // keep for class initialisation (call from initializer)
    }

}
