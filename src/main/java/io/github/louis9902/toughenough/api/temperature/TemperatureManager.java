package io.github.louis9902.toughenough.api.temperature;

import dev.onyxstudios.cca.api.v3.component.AutoSyncedComponent;
import io.github.louis9902.toughenough.api.debug.DebugMonitor;

public interface TemperatureManager extends AutoSyncedComponent {
    int getTarget();

    int getTemperature();

    int getRate();

    void update();

    boolean getDebug();

    void setDebug(boolean value);

    DebugMonitor getTargetMonitor();

    DebugMonitor getRateMonitor();
}
