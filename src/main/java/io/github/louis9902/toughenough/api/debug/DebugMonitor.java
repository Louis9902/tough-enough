package io.github.louis9902.toughenough.api.debug;

import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DebugMonitor implements Iterable<Map.Entry<String, String>> {

    protected final HashMap<String, String> values = new HashMap<>();

    public Optional<String> get(@NotNull String identifier) {
        return Optional.ofNullable(values.get(identifier));
    }

    public void add(@NotNull String identifier, @NotNull String information) {
        values.put(identifier, information);
    }

    public void encode(PacketByteBuf buf) {
        buf.writeInt(values.size());
        for (Map.Entry<String, String> entry : values.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeString(entry.getValue());
        }
    }

    public void decode(PacketByteBuf buf) {
        values.clear();
        int count = buf.readInt();
        for (int i = 0; i < count; ++i) {
            values.put(buf.readString(), buf.readString());
        }
    }

    @NotNull
    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return values.entrySet().iterator();
    }
}
