package io.github.louis9902.toughenough.temperature;

import io.github.louis9902.toughenough.ToughEnough;
import io.github.louis9902.toughenough.misc.DebugMonitor;
import io.github.louis9902.toughenough.temperature.api.TemperatureModifier;
import io.github.louis9902.toughenough.temperature.modifiers.BiomeModifier;
import io.github.louis9902.toughenough.temperature.modifiers.BlockProximityModifier;
import io.github.louis9902.toughenough.temperature.modifiers.HealthModifier;
import io.github.louis9902.toughenough.temperature.modifiers.TimeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static io.github.louis9902.toughenough.temperature.HeatManagerConstants.DEFAULT_RATE;
import static io.github.louis9902.toughenough.temperature.HeatManagerConstants.TEMPERATURE_EQUILIBRIUM;

public class TemperatureHelper {
    public static final ArrayList<TemperatureModifier> modifiers = new ArrayList<>();

    static {
        modifiers.add(new BiomeModifier(ToughEnough.identifier("biome")));
        modifiers.add(new BlockProximityModifier(ToughEnough.identifier("block")));
        modifiers.add(new TimeModifier(ToughEnough.identifier("time")));
        modifiers.add(new HealthModifier(ToughEnough.identifier("health")));
    }

    public int calculatePlayerTarget(@NotNull PlayerEntity player, @Nullable DebugMonitor monitor) {
        return modifiers.stream().mapToInt((modifier) -> modifier.applyTargetFromPlayer(player, monitor)).reduce(TEMPERATURE_EQUILIBRIUM, Integer::sum);
    }

    public int calculateBlockTarget(@NotNull World world, @NotNull BlockPos pos, @Nullable DebugMonitor monitor) {
        return modifiers.stream().filter((mod) -> !mod.isPlayerModifier()).mapToInt((mod) -> mod.applyTargetFromEnvironment(world, pos, monitor)).reduce(TEMPERATURE_EQUILIBRIUM, Integer::sum);
    }

    public int calculatePlayerRate(@NotNull PlayerEntity player, @Nullable DebugMonitor monitor) {
        return modifiers.stream().mapToInt((modifier) -> modifier.applyRateFromPlayer(player, monitor)).reduce(DEFAULT_RATE, Integer::sum);
    }
}
