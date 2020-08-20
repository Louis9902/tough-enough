package io.github.louis9902.toughenough.components;

import nerdhub.cardinal.components.api.component.extension.CopyableComponent;

public interface DrinkableComponent extends CopyableComponent<DrinkableComponent> {
    int getThirst();

    float getHydrationModifier();
}
