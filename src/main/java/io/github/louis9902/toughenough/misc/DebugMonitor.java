package io.github.louis9902.toughenough.misc;

import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

public class DebugMonitor extends DebugMonitorView {
    public void add(@NotNull String identifier, @NotNull String information) {
        values.put(identifier, information);
    }

    public void decode(PacketByteBuf buf) {
        values.clear();
        int count = buf.readInt();
        for (int i = 0; i < count; ++i) {
            values.put(buf.readString(), buf.readString());
        }
    }
}
