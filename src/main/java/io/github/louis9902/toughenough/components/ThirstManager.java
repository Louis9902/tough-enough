package io.github.louis9902.toughenough.components;

import nerdhub.cardinal.components.api.util.sync.EntitySyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface ThirstManager extends EntitySyncedComponent {

    int getThirst();

    float getHydration();

    float getExhaustion();

    void setThirst(int t);

    void setHydration(float h);

    void setExhaustion(float e);

    /**
     * This will run the internal logic of the {@link ThirstManager} and should be called every tick from the Player
     *
     * @param entity The player the thirst logic should be updated on
     */
    void update(PlayerEntity entity);

    /**
     * This will get the {@link Drinkable} from the ItemStack and add its values to the {@link ThirstManager}
     * appropriately. If the component is not present, the item will be ignored
     *
     * @param item The {@link ItemStack} that should be consumed
     */
    void drink(ItemStack item);

    default void addThirst(int t) {
        setThirst(getThirst() + t);
    }

    default void addExhaustion(float t) {
        setExhaustion(getExhaustion() + t);
    }

    default void addHydration(float t) {
        setHydration(getHydration() + t);
    }
}
