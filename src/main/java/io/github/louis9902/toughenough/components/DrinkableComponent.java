package io.github.louis9902.toughenough.components;

import nerdhub.cardinal.components.api.component.extension.CopyableComponent;

/**
 * This Component represents things that can be drank (probably items)
 * Thirst is the thirst level that will be restored
 * Hydration is the amount of thirst that is saved beyond the normal thirst bar (equivalent to saturation of hunger)
 */
public interface DrinkableComponent extends CopyableComponent<DrinkableComponent> {
    int getThirst();

    float getHydrationModifier();
}
