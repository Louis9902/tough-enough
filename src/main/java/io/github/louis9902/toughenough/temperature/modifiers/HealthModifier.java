package io.github.louis9902.toughenough.temperature.modifiers;

import io.github.louis9902.toughenough.api.temperature.Modifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class HealthModifier extends Modifier {
    private static final float HEALTH_THRESHOLD = 0.25F;
    private static final int RATE_EFFECT = -200;

    public HealthModifier(Identifier id) {
        super(id);
    }

    @Override
    public int calculateFromPlayer(@NotNull PlayerEntity player) {
        //When player is below 25% health, the temperature adjustment rate gets decreased
        return (player.getHealth() / player.getMaxHealth()) < HEALTH_THRESHOLD ? RATE_EFFECT : 0;
    }

    @Override
    public boolean isPlayerModifier() {
        return true;
    }
}
