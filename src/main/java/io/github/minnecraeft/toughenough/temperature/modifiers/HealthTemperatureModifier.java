package io.github.minnecraeft.toughenough.temperature.modifiers;

import io.github.minnecraeft.toughenough.ToughEnoughComponents;
import io.github.minnecraeft.toughenough.api.debug.DebugMonitor;
import io.github.minnecraeft.toughenough.api.temperature.TemperatureModifier;
import io.github.minnecraeft.toughenough.temperature.DefaultTemperatureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class HealthTemperatureModifier extends TemperatureModifier {

    private static final float HEALTH_THRESHOLD = 0.25F;
    private static final int RATE_EFFECT = -200;

    public HealthTemperatureModifier(Identifier id) {
        super(id);
    }

    @Override
    public int calculateFromPlayer(@NotNull PlayerEntity player) {
        // when player is below 25% health
        // the temperature adjustment rate gets decreased
        int result = (player.getHealth() / player.getMaxHealth()) < HEALTH_THRESHOLD ? RATE_EFFECT : 0;
        {
            DebugMonitor monitor = ToughEnoughComponents.DEBUGGER_MONITOR.get(player);
            if (monitor.isDebugging())
                monitor.section(DefaultTemperatureManager.DEBUG_RATE_MODIFIERS).putInt(getIdentifier().getPath(), result);
        }
        return result;
    }

    @Override
    public boolean isPlayerModifier() {
        return true;
    }
}
