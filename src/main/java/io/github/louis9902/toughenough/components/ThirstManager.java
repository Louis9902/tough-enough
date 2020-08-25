package io.github.louis9902.toughenough.components;

import dev.onyxstudios.cca.api.v3.component.AutoSyncedComponent;
import io.github.louis9902.toughenough.components.defaults.DefaultThirstManager;
import net.minecraft.item.ItemStack;

public interface ThirstManager extends AutoSyncedComponent {

    static int getMaxThirst() {
        return DefaultThirstManager.MAX_THIRST_LEVEL;
    }

    int getThirst();

    float getHydration();

    float getExhaustion();

    void setThirst(int t);

    void setHydration(float h);

    void setExhaustion(float e);

    /**
     * This will run the internal logic of the {@link ThirstManager} and should be called every tick from the Player
     */
    void update();

    /**
     * This will get the {@link Drink} from the ItemStack and add its values to the {@link ThirstManager}
     * appropriately. If the component is not present, the item will be ignored
     *
     * @param item The {@link ItemStack} that should be consumed
     */
    void drink(Drink drink);

    default void addThirst(int t) {
        setThirst(getThirst() + t);
    }

    default void addExhaustion(float t) {
        setExhaustion(getExhaustion() + t);
    }

    default void addHydration(float t) {
        setHydration(getHydration() + t);
    }

    default boolean isThirsty() {
        return getThirst() < getMaxThirst();
    }

}
