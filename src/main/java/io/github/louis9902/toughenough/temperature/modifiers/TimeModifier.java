package io.github.louis9902.toughenough.temperature.modifiers;

import io.github.louis9902.toughenough.misc.DebugMonitor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimeModifier extends DefaultTemperatureModifier {
    private final static int TIME_TARGET_SCALE = 2;

    public TimeModifier(Identifier id) {
        super(id);
    }

    @Override
    public int applyTargetFromEnvironment(@NotNull World world, @NotNull BlockPos pos, @Nullable DebugMonitor monitor) {
        long time = world.getTimeOfDay();
        //Scale time of day to temperature between -1 and 1
        //by modifying sin so that period is 24000 (length of one minecraft day)
        int result = (int) Math.round(Math.sin(time * ((2 * Math.PI) / 24000.0)) * TIME_TARGET_SCALE);
        addIfNotNull(monitor, "Time", Integer.toString(result));
        return result;
    }

    @Override
    public boolean isPlayerModifier() {
        return false;
    }
}
