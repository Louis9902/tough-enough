package io.github.louis9902.toughenough;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import io.github.louis9902.toughenough.StatusEffect.Thirst;
import io.github.louis9902.toughenough.components.DrinkableComponent;
import io.github.louis9902.toughenough.components.ThirstyManager;
import io.github.louis9902.toughenough.item.Canteen;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class ToughEnough implements ModInitializer {
    public static final String MOD_ID = "tough_enough";

    //removed mod item group for now as it kept crashing!
    public static final Item CANTEEN_ITEM = Registry.register(Registry.ITEM, identifier("canteen"), new Canteen(new Item.Settings().group(ItemGroup.MISC).maxDamage(4)));
    public static final StatusEffect THIRST_EFFECT = Registry.register(Registry.STATUS_EFFECT, identifier("thirst"), new Thirst(StatusEffectType.HARMFUL, 1));

    @Override
    public void onInitialize() {
    }

    public static Identifier identifier(String name) {
        return new Identifier(MOD_ID, name);
    }
}
