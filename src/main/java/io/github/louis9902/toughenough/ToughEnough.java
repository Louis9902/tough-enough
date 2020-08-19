package io.github.louis9902.toughenough;

import io.github.louis9902.toughenough.StatusEffect.Thirst;
import io.github.louis9902.toughenough.item.Canteen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class ToughEnough implements ModInitializer {
    public static String MOD_ID = "tough_enough";
    public static ItemGroup MOD_GROUP = FabricItemGroupBuilder.create(id("tough_enough")).build();

    public static final Item canteen = Registry.register(Registry.ITEM,id("canteen"),new Canteen(new Item.Settings().group(MOD_GROUP).maxDamage(4)));
    public static final StatusEffect thirst = Registry.register(Registry.STATUS_EFFECT,id("hunger"),new Thirst(StatusEffectType.HARMFUL,1));

    @Override
    public void onInitialize() {

    }

    public static Identifier id(String name){
        return new Identifier(MOD_ID,name);
    }
}
