package io.github.louis9902.toughenough.init;

import io.github.louis9902.toughenough.block.blockentity.ClimatizerBlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class ToughEnoughBlockEntities {

    public static final BlockEntityType<ClimatizerBlockEntity> CLIMATIZER_ENTITY_TYPE;

    static {
        CLIMATIZER_ENTITY_TYPE = RegistryHelpers.register("climatizer", BlockEntityType.Builder.create(ClimatizerBlockEntity::new, ToughEnoughBlocks.CLIMATIZER));
    }

    public static void register() {
        // keep for class initialisation (call from initializer)
    }
}
