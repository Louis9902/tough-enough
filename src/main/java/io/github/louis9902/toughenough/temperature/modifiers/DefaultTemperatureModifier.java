package io.github.louis9902.toughenough.temperature.modifiers;

import io.github.louis9902.toughenough.misc.DebugMonitor;
import io.github.louis9902.toughenough.temperature.api.TemperatureModifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DefaultTemperatureModifier implements TemperatureModifier {
    private final Identifier id;

    public DefaultTemperatureModifier(Identifier id) {
        this.id = id;
    }

    @Override
    public @NotNull Identifier getId() {
        return id;
    }

    protected void addIfNotNull(@Nullable DebugMonitor monitor, @NotNull String identifier, @NotNull String value) {
        if (monitor != null)
            monitor.add(identifier, value);
    }
}
