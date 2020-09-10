package io.github.minnecraeft.toughenough.api.temperature;

import io.github.minnecraeft.toughenough.ToughEnough;
import io.github.minnecraeft.toughenough.temperature.modifiers.BiomeTemperatureModifier;
import io.github.minnecraeft.toughenough.temperature.modifiers.BlockProximityTemperatureModifier;
import io.github.minnecraeft.toughenough.temperature.modifiers.HealthTemperatureModifier;
import io.github.minnecraeft.toughenough.temperature.modifiers.TimeTemperatureModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class TemperatureModifiers {

    private TemperatureModifiers() {
    }

    public static final ArrayList<TemperatureModifier> MODIFIERS_TARGET = new ArrayList<>();
    public static final ArrayList<TemperatureModifier> MODIFIERS_RATE = new ArrayList<>();

    static {
        MODIFIERS_TARGET.add(new BiomeTemperatureModifier(ToughEnough.identifier("biome")));
        MODIFIERS_TARGET.add(new BlockProximityTemperatureModifier(ToughEnough.identifier("block")));
        MODIFIERS_TARGET.add(new TimeTemperatureModifier(ToughEnough.identifier("time")));
    }

    static {
        MODIFIERS_RATE.add(new HealthTemperatureModifier(ToughEnough.identifier("health")));
    }

    public static int calcTargetForPlayer(@NotNull PlayerEntity player) {
        return MODIFIERS_TARGET.stream()
                .mapToInt((modifier) -> modifier.calculateFromPlayer(player))
                .reduce(TemperatureConstants.TEMPERATURE_EQUILIBRIUM, Integer::sum);
    }

    public static int calcTargetForBlock(@NotNull World world, @NotNull BlockPos pos) {
        return MODIFIERS_TARGET.stream()
                .filter(modifier -> !modifier.isPlayerModifier())
                .mapToInt((modifier) -> modifier.calculateFromEnvironment(world, pos))
                .reduce(TemperatureConstants.TEMPERATURE_EQUILIBRIUM, Integer::sum);
    }

    public static int calcRateForPlayer(@NotNull PlayerEntity player) {
        return MODIFIERS_RATE.stream()
                .mapToInt((modifier) -> modifier.calculateFromPlayer(player))
                .reduce(TemperatureConstants.DEFAULT_CHANGE_RATE, Integer::sum);
    }
}
