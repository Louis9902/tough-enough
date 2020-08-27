package io.github.louis9902.toughenough.temperature.modifiers;

import io.github.louis9902.toughenough.temperature.api.TemperatureModifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public abstract class DefaultTemperatureModifier implements TemperatureModifier {
    private final Identifier id;

    public DefaultTemperatureModifier(Identifier id) {
        this.id = id;
    }

    @Override
    public @NotNull Identifier getId() {
        return id;
    }
}
