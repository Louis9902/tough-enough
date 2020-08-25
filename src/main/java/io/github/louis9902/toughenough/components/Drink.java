package io.github.louis9902.toughenough.components;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;

/**
 * This Component represents things that can be drank (probably items)
 * Thirst is the thirst level that will be restored
 * Hydration is the amount of thirst that is saved beyond the normal thirst bar (equivalent to saturation of hunger)
 */
public interface Drink extends ComponentV3 {
    int getThirst();

    float getHydration();
}
