package io.github.louis9902.toughenough.misc;

import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class DebugMonitorView implements Iterable<Map.Entry<String, String>> {
    protected HashMap<String, String> values = new HashMap<>();

    public Optional<String> get(@NotNull String identifier) {
        return Optional.ofNullable(values.get(identifier));
    }

    public void encode(PacketByteBuf buf) {
        buf.writeInt(values.size());
        for (Map.Entry<String, String> entry : values.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeString(entry.getValue());
        }
    }

    @NotNull
    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return values.entrySet().iterator();
    }
}
