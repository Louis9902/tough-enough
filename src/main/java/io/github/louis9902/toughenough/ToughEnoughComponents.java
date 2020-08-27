package io.github.louis9902.toughenough;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import io.github.louis9902.toughenough.components.Drink;
import io.github.louis9902.toughenough.components.HeatManager;
import io.github.louis9902.toughenough.components.ThirstManager;
import io.github.louis9902.toughenough.components.defaults.DefaultDrink;
import io.github.louis9902.toughenough.components.defaults.DefaultHeatManager;
import io.github.louis9902.toughenough.components.defaults.DefaultThirstManager;
import io.github.louis9902.toughenough.item.DrinkItem;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.minecraft.item.Items;

import static io.github.louis9902.toughenough.ToughEnough.identifier;

public class ToughEnoughComponents implements ItemComponentInitializer, EntityComponentInitializer {

    public static final ComponentKey<Drink> DRINKABLE = ComponentRegistryV3.INSTANCE.getOrCreate(identifier("drinkable"), Drink.class);

    public static final ComponentKey<ThirstManager> THIRSTY = ComponentRegistryV3.INSTANCE.getOrCreate(identifier("thirsty"), ThirstManager.class);
    public static final ComponentKey<HeatManager> HEATY = ComponentRegistryV3.INSTANCE.getOrCreate(identifier("heaty"), HeatManager.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        //Heat and Thirst will only be saved when player returns from end, not when dying
        registry.registerForPlayers(THIRSTY, DefaultThirstManager::new, RespawnCopyStrategy.LOSSLESS_ONLY);
        registry.registerForPlayers(HEATY, DefaultHeatManager::new, RespawnCopyStrategy.LOSSLESS_ONLY);
    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
        registry.registerFor(item -> item == Items.BREAD, DRINKABLE, (item, stack) -> new DefaultDrink(2, 5.0F));
        //registry.registerFor(item -> item == ToughEnoughItems.CANTEEN_ITEM, DRINKABLE, ((item, stack) -> new DefaultDrink(2, 5.0F)));
        //DrinkItem item = new DrinkItem(new Item.Settings(), 0, 0.0f);
        registry.registerFor(DrinkItem.class::isInstance, DRINKABLE, (item, stack) -> ((DrinkItem) item).newComponent(stack));
    }
}
