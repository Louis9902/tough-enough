package io.github.louis9902.toughenough.temperature.modifiers;

import io.github.louis9902.toughenough.api.temperature.TemperatureModifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class TimeModifier extends TemperatureModifier {

    private final static int TIME_TARGET_SCALE = 2;

    public TimeModifier(Identifier id) {
        super(id);
    }

    @Override
    public int applyTargetFromEnvironment(@NotNull World world, @NotNull BlockPos pos) {
        long time = world.getTimeOfDay();
        //Scale time of day to temperature between -1 and 1
        //by modifying sin so that period is 24000 (length of one minecraft day)
        return (int) Math.round(Math.sin(time * ((2 * Math.PI) / 24000.0)) * TIME_TARGET_SCALE);
    }

    @Override
    public boolean isPlayerModifier() {
        return false;
    }
}
