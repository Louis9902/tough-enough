package io.github.louis9902.toughenough.temperature.modifiers;

import io.github.louis9902.toughenough.misc.DebugMonitor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HealthModifier extends DefaultTemperatureModifier {
    private static final float HEALTH_THRESHOLD = 0.25F;
    private static final int RATE_EFFECT = -200;

    public HealthModifier(Identifier id) {
        super(id);
    }

    @Override
    public int applyRateFromPlayer(@NotNull PlayerEntity playerEntity, @Nullable DebugMonitor monitor) {
        //When player is below 25% health, the temperature adjustment rate gets decreased
        int result = (playerEntity.getHealth() / playerEntity.getMaxHealth()) < HEALTH_THRESHOLD ? RATE_EFFECT : 0;
        addIfNotNull(monitor, "Health", Integer.toString(result));
        return result;
    }

    @Override
    public boolean isPlayerModifier() {
        return true;
    }
}
