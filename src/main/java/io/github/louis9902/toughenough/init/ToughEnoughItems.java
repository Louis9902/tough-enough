package io.github.louis9902.toughenough.init;

import com.google.common.collect.ImmutableList;
import io.github.louis9902.toughenough.item.CanteenItem;
import io.github.louis9902.toughenough.item.JuiceItem;
import io.github.louis9902.toughenough.item.drink.JuiceType;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import static io.github.louis9902.toughenough.ToughEnough.identifier;

public final class ToughEnoughItems {
    public static final ItemGroup GROUP = FabricItemGroupBuilder.build(identifier("general"), () -> new ItemStack(ToughEnoughItems.CANTEEN));

    public static final JuiceItem APPLE_JUICE;
    public static final JuiceItem BEETROOT_JUICE;
    public static final JuiceItem CACTUS_JUICE;
    public static final JuiceItem SWEET_BERRY_JUICE;
    public static final JuiceItem CARROT_JUICE;
    public static final JuiceItem MELON_JUICE;
    public static final JuiceItem PUMPKIN_JUICE;

    public static final CanteenItem CANTEEN;
    public static final Item THERMOMETER;
    public static final Item FILTER;
    public static final Item MAGMA_SHARD;
    public static final Item ICE_SHARD;

    public static final Item CLIMATIZER_ITEM;

    public static final ImmutableList<JuiceItem> JUICES;

    static {
        CANTEEN = RegistryHelpers.register("canteen", new CanteenItem(ItemSettings()));
        THERMOMETER = RegistryHelpers.register("thermometer", new Item(ItemSettings()));
        FILTER = RegistryHelpers.register("filter", new Item(ItemSettings()));
        CLIMATIZER_ITEM = RegistryHelpers.register("climatizer", new BlockItem(ToughEnoughBlocks.CLIMATIZER, ItemSettings()));
        MAGMA_SHARD = RegistryHelpers.register("magma_shard", new Item(ItemSettings()));
        ICE_SHARD = RegistryHelpers.register("ice_shard", new Item(ItemSettings()));

        JUICES = ImmutableList.of(
                (APPLE_JUICE = RegistryHelpers.register("juice_apple", new JuiceItem(ItemSettings(), JuiceType.APPLE))),
                (BEETROOT_JUICE = RegistryHelpers.register("juice_beetroot", new JuiceItem(ItemSettings(), JuiceType.BEETROOT))),
                (CACTUS_JUICE = RegistryHelpers.register("juice_cactus", new JuiceItem(ItemSettings(), JuiceType.CACTUS))),
                (CARROT_JUICE = RegistryHelpers.register("juice_carrot", new JuiceItem(ItemSettings(), JuiceType.CARROT))),
                (MELON_JUICE = RegistryHelpers.register("juice_melon", new JuiceItem(ItemSettings(), JuiceType.MELON))),
                (PUMPKIN_JUICE = RegistryHelpers.register("juice_pumpkin", new JuiceItem(ItemSettings(), JuiceType.PUMPKIN))),
                (SWEET_BERRY_JUICE = RegistryHelpers.register("juice_sweet_berry", new JuiceItem(ItemSettings(), JuiceType.SWEET_BERRY)))
        );
    }

    private static Item.Settings ItemSettings() {
        return new Item.Settings().group(GROUP);
    }

    public static void register() {
        // keep for class initialisation (call from initializer)
    }

}
