package io.github.louis9902.toughenough.init;

import io.github.louis9902.toughenough.fluid.PurifiedWater;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class ToughEnoughFluids {

    public static final PurifiedWater STILL_PURIFIED_WATER;
    public static final PurifiedWater FLOWING_PURIFIED_WATER;
    public static final Item PURIFIED_WATER_BUCKET;
    public static final Block PURIFIED_WATER_BLOCK;

    static {
        STILL_PURIFIED_WATER = RegistryHelpers.register("purified_still", new PurifiedWater.Still());
        FLOWING_PURIFIED_WATER = RegistryHelpers.register("purified_flowing", new PurifiedWater.Flowing());
        PURIFIED_WATER_BUCKET = RegistryHelpers.register("purified_bucket", new BucketItem(STILL_PURIFIED_WATER, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
        PURIFIED_WATER_BLOCK = RegistryHelpers.register("purified_block", new FluidBlock(STILL_PURIFIED_WATER, FabricBlockSettings.copyOf(Blocks.WATER)) {
        });
    }

    public static void register() {
        // keep for class initialisation (call from initializer)
    }
}
