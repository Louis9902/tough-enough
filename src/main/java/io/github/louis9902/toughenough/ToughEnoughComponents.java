package io.github.louis9902.toughenough;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import io.github.louis9902.toughenough.components.Drink;
import io.github.louis9902.toughenough.components.TemperatureManager;
import io.github.louis9902.toughenough.components.ThirstManager;
import io.github.louis9902.toughenough.components.defaults.DefaultTemperatureManager;
import io.github.louis9902.toughenough.components.defaults.DefaultThirstManager;
import io.github.louis9902.toughenough.item.DrinkItem;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;

import static io.github.louis9902.toughenough.ToughEnough.identifier;

public class ToughEnoughComponents implements ItemComponentInitializer, EntityComponentInitializer {

    public static final ComponentKey<Drink> DRINKABLE = ComponentRegistryV3.INSTANCE.getOrCreate(identifier("drinkable"), Drink.class);

    public static final ComponentKey<ThirstManager> THIRSTY = ComponentRegistryV3.INSTANCE.getOrCreate(identifier("thirsty"), ThirstManager.class);
    public static final ComponentKey<TemperatureManager> HEATY = ComponentRegistryV3.INSTANCE.getOrCreate(identifier("heaty"), TemperatureManager.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        //Heat and Thirst will only be saved when player returns from end, not when dying
        registry.registerForPlayers(THIRSTY, DefaultThirstManager::new, RespawnCopyStrategy.LOSSLESS_ONLY);
        registry.registerForPlayers(HEATY, DefaultTemperatureManager::new, RespawnCopyStrategy.LOSSLESS_ONLY);
    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
        registry.registerFor(DrinkItem.class::isInstance, DRINKABLE, (item, stack) -> ((DrinkItem) item).component(stack));
    }
}
