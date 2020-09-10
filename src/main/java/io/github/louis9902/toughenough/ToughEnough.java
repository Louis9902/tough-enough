package io.github.louis9902.toughenough;

import io.github.louis9902.toughenough.init.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;


public class ToughEnough implements ModInitializer {
    private static final Identifier MAGMA_BLOCK_LOOT_TABLE_ID = new Identifier("minecraft", "blocks/magma_block");
    private static final Identifier ICE_BLOCK_LOOT_TABLE_ID = new Identifier("minecraft", "blocks/ice");
    private static final Identifier PACKED_ICE_BLOCK_LOOT_TABLE_ID = new Identifier("minecraft", "blocks/packed_ice");
    private static final Identifier BLUE_ICE_BLOCK_LOOT_TABLE_ID = new Identifier("minecraft", "blocks/blue_ice");

    public static final String ID = "tough_enough";

    public static Identifier identifier(String name) {
        return new Identifier(ID, name);
    }

    @Override
    public void onInitialize() {
        ToughEnoughItems.register();
        ToughEnoughRegistries.register();
        ToughEnoughStatusEffects.register();
        ToughEnoughTags.register();
        ToughEnoughFluids.register();
        ToughEnoughBlockEntities.register();
        ToughEnoughScreenHandlers.register();
        ToughEnoughBlocks.register();

        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, lootTableSetter) -> {
            if (id.equals(MAGMA_BLOCK_LOOT_TABLE_ID)) {
                LootPool pool = FabricLootPoolBuilder.builder()
                        .withEntry(ItemEntry.builder(ToughEnoughItems.MAGMA_SHARD).build())
                        .withCondition(RandomChanceLootCondition.builder(0.5f).build())
                        .rolls(ConstantLootTableRange.create(1))
                        .build();

                supplier.withPool(pool);
            } else if (id.equals(ICE_BLOCK_LOOT_TABLE_ID) || id.equals(PACKED_ICE_BLOCK_LOOT_TABLE_ID) || id.equals(BLUE_ICE_BLOCK_LOOT_TABLE_ID)) {
                LootPool pool = FabricLootPoolBuilder.builder()
                        .withEntry(ItemEntry.builder(ToughEnoughItems.ICE_SHARD).build())
                        .withCondition(RandomChanceLootCondition.builder(0.5f).build())
                        .rolls(ConstantLootTableRange.create(1))
                        .build();

                supplier.withPool(pool);
            }
        });
    }
}
