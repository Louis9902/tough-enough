package io.github.louis9902.toughenough.init;

import io.github.louis9902.toughenough.block.ClimatizerBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class ToughEnoughBlocks {
    public static final Block CLIMATIZER;

    static {
        CLIMATIZER = RegistryHelpers.register("climatizer", new ClimatizerBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));
    }

    @SuppressWarnings("EmptyMethod")
    public static void register() {
        // keep for class initialisation (call from initializer)
    }
}
