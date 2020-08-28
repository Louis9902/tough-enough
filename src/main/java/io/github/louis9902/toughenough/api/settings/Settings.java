package io.github.louis9902.toughenough.api.settings;

import dev.onyxstudios.cca.api.v3.component.AutoSyncedComponent;

public interface Settings extends AutoSyncedComponent {

    boolean isThirstEnabled();

    boolean isTemperatureEnabled();

    void setThirstEnabled(boolean x);

    void setTemperatureEnabled(boolean x);

}
