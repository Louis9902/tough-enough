package io.github.louis9902.toughenough.init;

import com.google.common.collect.ImmutableList;
import io.github.louis9902.toughenough.ToughEnough;
import io.github.louis9902.toughenough.item.CanteenItem;
import io.github.louis9902.toughenough.item.JuiceItem;
import io.github.louis9902.toughenough.item.ThermometerItem;
import io.github.louis9902.toughenough.item.drink.JuiceType;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import static net.minecraft.util.registry.Registry.ITEM;

public final class ToughEnoughItems {

    public static final ItemGroup GROUP;

    public static final CanteenItem CANTEEN;
    public static final ThermometerItem THERMOMETER;

    public static final JuiceItem APPLE_JUICE;
    public static final JuiceItem BEETROOT_JUICE;
    public static final JuiceItem CACTUS_JUICE;
    public static final JuiceItem SWEET_BERRY_JUICE;
    public static final JuiceItem CARROT_JUICE;
    public static final JuiceItem MELON_JUICE;
    public static final JuiceItem PUMPKIN_JUICE;

    public static final Item THERMOMETER;
    public static final Item FILTER;

    public static final ImmutableList<JuiceItem> JUICES;

    static {
        CANTEEN = register("canteen", new CanteenItem(newSettings()));

        GROUP = FabricItemGroupBuilder.build(ToughEnough.identifier("general"), () -> new ItemStack(CANTEEN));

        JUICES = ImmutableList.of(
                (APPLE_JUICE = register("juice_apple", new JuiceItem(newSettings(), JuiceType.APPLE))),
                (BEETROOT_JUICE = register("juice_beetroot", new JuiceItem(newSettings(), JuiceType.BEETROOT))),
                (CACTUS_JUICE = register("juice_cactus", new JuiceItem(newSettings(), JuiceType.CACTUS))),
                (CARROT_JUICE = register("juice_carrot", new JuiceItem(newSettings(), JuiceType.CARROT))),
                (MELON_JUICE = register("juice_melon", new JuiceItem(newSettings(), JuiceType.MELON))),
                (PUMPKIN_JUICE = register("juice_pumpkin", new JuiceItem(newSettings(), JuiceType.PUMPKIN))),
                (SWEET_BERRY_JUICE = register("juice_sweet_berry", new JuiceItem(newSettings(), JuiceType.SWEET_BERRY)))
        );

        THERMOMETER = register("thermometer", new Item(newSettings()));
        FILTER = register("filter", new Item(newSettings()));
    }

    private static Item.Settings newSettings() {
        return new Item.Settings().group(GROUP);
    }

    private static <T extends Item> T register(String name, T item) {
        return Registry.register(ITEM, ToughEnough.identifier(name), item);
    }

    public static void register() {
        // keep for class initialisation (call from initializer)
    }

}
