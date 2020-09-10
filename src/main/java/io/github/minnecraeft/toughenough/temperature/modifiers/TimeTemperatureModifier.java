package io.github.minnecraeft.toughenough.temperature.modifiers;

import io.github.minnecraeft.toughenough.ToughEnoughComponents;
import io.github.minnecraeft.toughenough.api.debug.DebugMonitor;
import io.github.minnecraeft.toughenough.api.temperature.TemperatureModifier;
import io.github.minnecraeft.toughenough.temperature.DefaultTemperatureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class TimeTemperatureModifier extends TemperatureModifier {

    private static final int TIME_TARGET_SCALE = 2;
    private static final float DAY_TIME_LENGTH = 24000.0f;

    public TimeTemperatureModifier(Identifier id) {
        super(id);
    }

    @Override
    public int calculateFromPlayer(@NotNull PlayerEntity player) {
        int result = super.calculateFromPlayer(player);
        {
            DebugMonitor monitor = ToughEnoughComponents.DEBUGGER_MONITOR.get(player);
            if (monitor.isDebugging())
                monitor.section(DefaultTemperatureManager.DEBUG_TARGET_MODIFIERS).putInt(getIdentifier().getPath(), result);
        }
        return result;
    }

    @Override
    public int calculateFromEnvironment(@NotNull World world, @NotNull BlockPos pos) {
        // scale time of day to temperature between -1 and 1
        // by modifying sin so that period is 24000
        long time = world.getTimeOfDay();
        return (int) Math.round(Math.sin(time * ((2 * Math.PI) / DAY_TIME_LENGTH)) * TIME_TARGET_SCALE);
    }

    @Override
    public boolean isPlayerModifier() {
        return false;
    }
}
