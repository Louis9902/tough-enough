package io.github.minnecraeft.toughenough.init;

import io.github.minnecraeft.toughenough.ToughEnough;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BlockEntityType.Builder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import static net.minecraft.util.registry.Registry.*;

public class RegistryHelpers {
    static <T extends Fluid> T register(String name, T fluid) {
        return Registry.register(FLUID, ToughEnough.identifier(name), fluid);
    }

    static <T extends Item> T register(String name, T item) {
        return Registry.register(ITEM, ToughEnough.identifier(name), item);
    }

    static <T extends StatusEffect> T register(String name, T effect) {
        return Registry.register(STATUS_EFFECT, ToughEnough.identifier(name), effect);
    }

    static <T extends Block> T register(String name, T effect) {
        return Registry.register(BLOCK, ToughEnough.identifier(name), effect);
    }

    static <T extends BlockEntity> BlockEntityType<T> register(String name, Builder<T> builder) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, ToughEnough.identifier(name), builder.build(null));
    }

}
