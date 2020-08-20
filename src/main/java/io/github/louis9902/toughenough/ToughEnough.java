package io.github.louis9902.toughenough;

import io.github.louis9902.toughenough.StatusEffect.Thirst;
import io.github.louis9902.toughenough.components.DrinkableComponent;
import io.github.louis9902.toughenough.components.ThirstyManager;
import io.github.louis9902.toughenough.components.implementations.DrinkableComponentImpl;
import io.github.louis9902.toughenough.components.implementations.ThirstyManagerImpl;
import io.github.louis9902.toughenough.item.Canteen;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.EntityComponentCallback;
import nerdhub.cardinal.components.api.event.ItemComponentCallbackV2;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class ToughEnough implements ModInitializer {
    public static String MOD_ID = "tough_enough";
    public static ItemGroup MOD_GROUP = FabricItemGroupBuilder.create(identifier("tough_enough")).build();

    public static final Item CANTEEN_ITEM = Registry.register(Registry.ITEM, identifier("canteen"), new Canteen(new Item.Settings().group(MOD_GROUP).maxDamage(4)));
    public static final StatusEffect THIRST_EFFECT = Registry.register(Registry.STATUS_EFFECT, identifier("thirst"), new Thirst(StatusEffectType.HARMFUL, 1));

    public static final ComponentType<DrinkableComponent> DRINKABLE = ComponentRegistry.INSTANCE.registerIfAbsent(identifier("drinkable"), DrinkableComponent.class);
    public static final ComponentType<ThirstyManager> THIRSTY = ComponentRegistry.INSTANCE.registerIfAbsent(identifier("thirsty"), ThirstyManager.class);

    @Override
    public void onInitialize() {
        //attachComponents();
        EntityComponentCallback.register(THIRSTY, PlayerEntity.class,(ThirstyManagerImpl::new));
        ItemComponentCallbackV2.register(DRINKABLE, CANTEEN_ITEM,(item, stack) -> new DrinkableComponentImpl(4,2.0F));
    }

    public static Identifier identifier(String name) {
        return new Identifier(MOD_ID, name);
    }

    private void attachComponents(){
        ItemComponentCallbackV2.register(DRINKABLE, Items.APPLE, (item, stack) -> new DrinkableComponentImpl(3,1.0f));
        ItemComponentCallbackV2.register(DRINKABLE, Items.MELON_SLICE, (item, stack) -> new DrinkableComponentImpl(1,1.0f));
        ItemComponentCallbackV2.register(DRINKABLE, Items.BAKED_POTATO, (item, stack) -> new DrinkableComponentImpl(2,1.0f));
        ItemComponentCallbackV2.register(DRINKABLE, Items.BEETROOT, (item, stack) -> new DrinkableComponentImpl(2,1.0f));
        ItemComponentCallbackV2.register(DRINKABLE, Items.CARROT, (item, stack) -> new DrinkableComponentImpl(1,1.0f));
        ItemComponentCallbackV2.register(DRINKABLE, Items.GOLDEN_APPLE, (item, stack) -> new DrinkableComponentImpl(1,1.0f));
        ItemComponentCallbackV2.register(DRINKABLE, Items.ENCHANTED_GOLDEN_APPLE, (item, stack) -> new DrinkableComponentImpl(2,1.0f));
        ItemComponentCallbackV2.register(DRINKABLE, Items.HONEY_BOTTLE, (item, stack) -> new DrinkableComponentImpl(6,1.0f));
    }
}
