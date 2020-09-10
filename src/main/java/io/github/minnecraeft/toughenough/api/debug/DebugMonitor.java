package io.github.minnecraeft.toughenough.api.debug;

import dev.onyxstudios.cca.api.v3.component.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;

public interface DebugMonitor extends AutoSyncedComponent {

    CompoundTag section(String name);

    CompoundTag section(String... names);

    boolean isDebugging();

    void setDebugging(boolean active);

    void checkForSync();
}
