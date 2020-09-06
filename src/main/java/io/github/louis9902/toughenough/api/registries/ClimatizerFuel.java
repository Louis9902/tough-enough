package io.github.louis9902.toughenough.api.registries;

import org.jetbrains.annotations.NotNull;

public class ClimatizerFuel {

    private final FuelType fuelType;
    private final int duration;

    public enum FuelType {HEATING, COOLING}

    public ClimatizerFuel(@NotNull FuelType fuelType, int duration) {
        this.fuelType = fuelType;
        this.duration = duration;
    }

    public @NotNull FuelType getFuelType() {
        return fuelType;
    }

    public int getDuration() {
        return duration;
    }
}
