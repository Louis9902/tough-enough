package io.github.louis9902.toughenough.temperature;

public final class TemperatureManagerConstants {

    public static final int MIN_TEMPERATURE_TARGET = -10;
    public static final int MAX_TEMPERATURE_TARGET = 10;

    public static final int DEFAULT_TEMPERATURE_TARGET = 0;

    public static final int TEMPERATURE_EQUILIBRIUM = 0;

    //100 ticks is 5 seconds
    public static final int MIN_CHANGE_RATE = 100;
    //2400 ticks is 2 minutes
    public static final int MAX_CHANGE_RATE = 2400;
    //600 ticks is 30 seconds
    public static final int DEFAULT_CHANGE_RATE = 200;
}
