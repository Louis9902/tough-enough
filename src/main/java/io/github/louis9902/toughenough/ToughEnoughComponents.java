package io.github.louis9902.toughenough;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import io.github.louis9902.toughenough.api.thirst.Drink;
import io.github.louis9902.toughenough.api.temperature.TemperatureManager;
import io.github.louis9902.toughenough.api.thirst.ThirstManager;
import io.github.louis9902.toughenough.components.DefaultTemperatureManager;
import io.github.louis9902.toughenough.components.DefaultThirstManager;
import io.github.louis9902.toughenough.item.DrinkItem;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;

import static io.github.louis9902.toughenough.ToughEnough.identifier;

@SuppressWarnings("UnstableApiUsage")
public class ToughEnoughComponents implements ItemComponentInitializer, EntityComponentInitializer {

    public static final ComponentKey<Drink> DRINKABLE = ComponentRegistryV3.INSTANCE.getOrCreate(identifier("drinkable"), Drink.class);

    public static final ComponentKey<ThirstManager> THIRST_MANAGER;
    public static final ComponentKey<TemperatureManager> TEMPERATURE_MANAGER;

    static {
        THIRST_MANAGER = ComponentRegistryV3.INSTANCE.getOrCreate(identifier("thirsty"), ThirstManager.class);
        TEMPERATURE_MANAGER = ComponentRegistryV3.INSTANCE.getOrCreate(identifier("heaty"), TemperatureManager.class);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        //Heat and Thirst will only be saved when player returns from end, not when dying
        registry.registerForPlayers(THIRST_MANAGER, DefaultThirstManager::new, RespawnCopyStrategy.LOSSLESS_ONLY);
        registry.registerForPlayers(TEMPERATURE_MANAGER, DefaultTemperatureManager::new, RespawnCopyStrategy.LOSSLESS_ONLY);
    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
        registry.registerFor(DrinkItem.class::isInstance, DRINKABLE, (item, stack) -> ((DrinkItem) item).componentToAttach(stack));
    }
}
