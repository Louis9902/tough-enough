package io.github.louis9902.toughenough.api.thirst;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import org.jetbrains.annotations.NotNull;

/**
 * This Component represents
 * a marker for stacks which should be able to be consumed by the player and restore thirst as well as hydration.
 * The consumption can also have side effects like thirst. For the possibility of such effects see
 * {@link Drink#getPoisonChance()}.
 */
public interface Drink extends ComponentV3 {

    @NotNull Modifiers getModifiers();

    void setModifiers(@NotNull Modifiers modifiers);

    default int getThirst() {
        return getModifiers().getThirst();
    }

    default float getHydration() {
        return getModifiers().getHydration();
    }

    default float getPoisonChance() {
        return getModifiers().getPoisonChance();
    }

    interface Modifiers {

        int getThirst();

        float getHydration();

        float getPoisonChance();
    }
}
