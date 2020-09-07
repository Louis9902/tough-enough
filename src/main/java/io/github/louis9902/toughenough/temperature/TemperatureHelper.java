package io.github.louis9902.toughenough.temperature;

import io.github.louis9902.toughenough.ToughEnough;
import io.github.louis9902.toughenough.api.debug.DebugMonitor;
import io.github.louis9902.toughenough.api.temperature.Modifier;
import io.github.louis9902.toughenough.temperature.modifiers.BiomeModifier;
import io.github.louis9902.toughenough.temperature.modifiers.BlockProximityModifier;
import io.github.louis9902.toughenough.temperature.modifiers.HealthModifier;
import io.github.louis9902.toughenough.temperature.modifiers.TimeModifier;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static io.github.louis9902.toughenough.temperature.TemperatureManagerConstants.DEFAULT_CHANGE_RATE;
import static io.github.louis9902.toughenough.temperature.TemperatureManagerConstants.TEMPERATURE_EQUILIBRIUM;

public final class TemperatureHelper {

    public static final ArrayList<Modifier> MODIFIERS_TARGET = new ArrayList<>();
    public static final ArrayList<Modifier> MODIFIERS_RATE = new ArrayList<>();

    private static final Registry<Modifier> RATE_MODIFIERS = newRegistry(Modifier.class, "rate_modifiers");
    private static final Registry<Modifier> TARGET_MODIFIERS = newRegistry(Modifier.class, "target_modifiers");

    private static <T> Registry<T> newRegistry(Class<T> clazz, String name) {
        return FabricRegistryBuilder.createSimple(clazz, ToughEnough.identifier(name)).buildAndRegister();
    }

    static {
        MODIFIERS_TARGET.add(new BiomeModifier(ToughEnough.identifier("biome")));
        MODIFIERS_TARGET.add(new BlockProximityModifier(ToughEnough.identifier("block")));
        MODIFIERS_TARGET.add(new TimeModifier(ToughEnough.identifier("time")));
        MODIFIERS_RATE.add(new HealthModifier(ToughEnough.identifier("health")));
    }

    public static int calcTargetForPlayer(@NotNull PlayerEntity player, @Nullable DebugMonitor monitor) {
        return MODIFIERS_TARGET.stream()
                .mapToInt((modifier) -> {
                    int value = modifier.calculateFromPlayer(player);
                    if (monitor != null && value > 0)
                        monitor.add(modifier.getIdentifier().getPath(), Integer.toString(value));
                    return value;
                })
                .reduce(TEMPERATURE_EQUILIBRIUM, Integer::sum);
    }

    public static int calcTargetForBlock(@NotNull World world, @NotNull BlockPos pos, @Nullable DebugMonitor monitor) {
        return MODIFIERS_TARGET.stream()
                .filter(modifier -> !modifier.isPlayerModifier())
                .mapToInt((modifier) -> {
                    int value = modifier.calculateFromEnvironment(world, pos);
                    if (monitor != null && value > 0)
                        monitor.add(modifier.getIdentifier().getPath(), Integer.toString(value));
                    return value;
                })
                .reduce(TEMPERATURE_EQUILIBRIUM, Integer::sum);
    }

    public static int calcRateForPlayer(@NotNull PlayerEntity player, @Nullable DebugMonitor monitor) {
        return MODIFIERS_RATE.stream()
                .mapToInt((modifier) -> {
                    int value = modifier.calculateFromPlayer(player);
                    if (monitor != null && value > 0)
                        monitor.add(modifier.getIdentifier().getPath(), Integer.toString(value));
                    return value;
                })
                .reduce(DEFAULT_CHANGE_RATE, Integer::sum);
    }
}
