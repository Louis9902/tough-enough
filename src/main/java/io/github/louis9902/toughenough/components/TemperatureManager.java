package io.github.louis9902.toughenough.components;

import dev.onyxstudios.cca.api.v3.component.AutoSyncedComponent;
import io.github.louis9902.toughenough.misc.DebugMonitorView;

public interface TemperatureManager extends AutoSyncedComponent {
    int getTarget();

    int getTemperature();

    int getRate();

    void update();

    boolean getDebug();

    void setDebug(boolean value);

    DebugMonitorView getTargetMonitor();

    DebugMonitorView getRateMonitor();
}
