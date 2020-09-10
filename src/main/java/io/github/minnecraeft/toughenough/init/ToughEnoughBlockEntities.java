package io.github.minnecraeft.toughenough.init;

import io.github.minnecraeft.toughenough.block.entity.ClimatizerBlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class ToughEnoughBlockEntities {

    public static final BlockEntityType<ClimatizerBlockEntity> CLIMATIZER_ENTITY_TYPE;

    static {
        CLIMATIZER_ENTITY_TYPE = RegistryHelpers.register("climatizer", BlockEntityType.Builder.create(ClimatizerBlockEntity::new, ToughEnoughBlocks.CLIMATIZER));
    }

    @SuppressWarnings("EmptyMethod")
    public static void register() {
        // keep for class initialisation (call from initializer)
    }
}
