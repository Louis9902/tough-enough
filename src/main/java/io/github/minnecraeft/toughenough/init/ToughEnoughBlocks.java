package io.github.minnecraeft.toughenough.init;

import io.github.minnecraeft.toughenough.block.ClimatizerBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

public class ToughEnoughBlocks {
    public static final Block CLIMATIZER;

    static {
        CLIMATIZER = RegistryHelpers.register("climatizer", new ClimatizerBlock(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.METAL).strength(0.2f)));
    }

    @SuppressWarnings("EmptyMethod")
    public static void register() {
        // keep for class initialisation (call from initializer)
    }
}
