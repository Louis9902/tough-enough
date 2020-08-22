package io.github.louis9902.toughenough;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import io.github.louis9902.toughenough.components.Drinkable;
import io.github.louis9902.toughenough.components.ThirstManager;
import io.github.louis9902.toughenough.components.defaults.DefaultDrinkable;
import io.github.louis9902.toughenough.components.defaults.DefaultThirstManager;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.item.Items;

import static io.github.louis9902.toughenough.ToughEnough.identifier;

public class ToughEnoughComponents implements ItemComponentInitializer, EntityComponentInitializer {

    public static final ComponentType<Drinkable> DRINKABLE = ComponentRegistry.INSTANCE.registerStatic(identifier("drinkable"), Drinkable.class);
    public static final ComponentType<ThirstManager> THIRSTY = ComponentRegistry.INSTANCE.registerStatic(identifier("thirsty"), ThirstManager.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(THIRSTY, DefaultThirstManager::new);
    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
        registry.registerFor(item -> item == Items.BREAD, DRINKABLE, (item, stack) -> new DefaultDrinkable(2, 5.0F));
        // lets try giving it the identifier instead of a predicate maybe that works
        registry.registerFor(ToughEnough.identifier("canteen"), DRINKABLE, ((item, stack) -> new DefaultDrinkable(2, 5.0F)));
    }
}
