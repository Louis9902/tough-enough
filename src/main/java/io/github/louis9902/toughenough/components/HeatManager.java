package io.github.louis9902.toughenough.components;

import dev.onyxstudios.cca.api.v3.component.AutoSyncedComponent;

public interface HeatManager extends AutoSyncedComponent {
    int getTemperature();

    void setTemperature(int t);

    void update();
}
