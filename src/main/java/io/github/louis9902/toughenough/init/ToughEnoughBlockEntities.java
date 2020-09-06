package io.github.louis9902.toughenough.init;

import io.github.louis9902.toughenough.ToughEnough;
import io.github.louis9902.toughenough.block.blockentity.ClimatizerBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class ToughEnoughBlockEntities {

    public static final BlockEntityType<ClimatizerBlockEntity> CLIMATIZER_ENTITY_TYPE;

    static {
        CLIMATIZER_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, ToughEnough.identifier("climatizer"), BlockEntityType.Builder.create(ClimatizerBlockEntity::new, ToughEnoughBlocks.CLIMATIZER).build(null));

        //CLIMATIZER_ENTITY_TYPE = RegistryHelpers.register("climatizer", BlockEntityType.Builder.create(ClimatizerEntity::new, ToughEnoughBlocks.CLIMATIZER));
    }

    public static void register() {
        // keep for class initialisation (call from initializer)
    }
}
