package io.github.louis9902.toughenough;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import io.github.louis9902.toughenough.components.DrinkableComponent;
import io.github.louis9902.toughenough.components.ThirstManager;
import io.github.louis9902.toughenough.components.implementations.DrinkableComponentImpl;
import io.github.louis9902.toughenough.components.implementations.ThirstManagerImpl;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.item.Items;

import static io.github.louis9902.toughenough.ToughEnough.identifier;

public class MyComponents implements ItemComponentInitializer, EntityComponentInitializer {
    public static final ComponentType<DrinkableComponent> DRINKABLE = ComponentRegistry.INSTANCE.registerStatic(identifier("drinkable"), DrinkableComponent.class);
    public static final ComponentType<ThirstManager> THIRSTY = ComponentRegistry.INSTANCE.registerStatic(identifier("thirsty"), ThirstManager.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(THIRSTY, ThirstManagerImpl::new);
    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
        registry.registerFor(item -> item == Items.BREAD, DRINKABLE, (item, stack) -> new DrinkableComponentImpl(2, 5.0F));
        //lets try giving it the identifier instead of a predicate maybe that works
        registry.registerFor(ToughEnough.identifier("canteen"), DRINKABLE, ((item, stack) -> new DrinkableComponentImpl(2, 5.0F)));
    }
}