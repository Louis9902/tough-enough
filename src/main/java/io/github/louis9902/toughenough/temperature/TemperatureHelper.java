package io.github.louis9902.toughenough.temperature;

import io.github.louis9902.toughenough.ToughEnough;
import io.github.louis9902.toughenough.api.debug.DebugMonitor;
import io.github.louis9902.toughenough.api.temperature.TemperatureModifier;
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

    public static int calcTargetForPlayer(@NotNull PlayerEntity player, @Nullable DebugMonitor monitor) {
        return modifiers.stream()
                .mapToInt((modifier) -> {
                    int value = modifier.applyTargetFromPlayer(player);
                    if (monitor != null) monitor.add(modifier.getIdentifier().getPath(), Integer.toString(value));
                    return value;
                })
                .reduce(TEMPERATURE_EQUILIBRIUM, Integer::sum);
    }

    public static int calcTargetForBlock(@NotNull World world, @NotNull BlockPos pos, @Nullable DebugMonitor monitor) {
        return modifiers.stream()
                .filter(modifier -> !modifier.isPlayerModifier())
                .mapToInt((modifier) -> {
                    int value = modifier.applyTargetFromEnvironment(world, pos);
                    if (monitor != null) monitor.add(modifier.getIdentifier().getPath(), Integer.toString(value));
                    return value;
                })
                .reduce(TEMPERATURE_EQUILIBRIUM, Integer::sum);
    }

    public static int calcRateForPlayer(@NotNull PlayerEntity player, @Nullable DebugMonitor monitor) {
        return modifiers.stream()
                .mapToInt((modifier) -> {
                    int value = modifier.applyRateFromPlayer(player);
                    if (monitor != null) monitor.add(modifier.getIdentifier().getPath(), Integer.toString(value));
                    return value;
                })
                .reduce(DEFAULT_RATE, Integer::sum);
    }
}
