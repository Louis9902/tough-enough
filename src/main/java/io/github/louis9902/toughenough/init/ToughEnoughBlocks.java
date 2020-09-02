package io.github.louis9902.toughenough.init;

import io.github.louis9902.toughenough.block.Climatizer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class ToughEnoughBlocks {
    public static final Block CLIMATIZER;

    static {
        CLIMATIZER = RegistryHelpers.register("climatizer", new Climatizer(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));
    }

    public static void register() {
        // keep for class initialisation (call from initializer)
    }
}
