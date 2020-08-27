package io.github.louis9902.toughenough.init;

import com.google.common.collect.ImmutableList;
import io.github.louis9902.toughenough.ToughEnough;
import io.github.louis9902.toughenough.item.CanteenItem;
import io.github.louis9902.toughenough.item.JuiceItem;
import io.github.louis9902.toughenough.item.drink.JuiceType;
import io.github.louis9902.toughenough.item.Thermometer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.registry.Registry.ITEM;

public final class ToughEnoughItems {

    public static final CanteenItem CANTEEN_ITEM;
    public static final Thermometer THERMOMETER_ITEM;

    public static final JuiceItem APPLE_JUICE;
    public static final JuiceItem BEETROOT_JUICE;
    public static final JuiceItem CACTUS_JUICE;
    public static final JuiceItem SWEET_BERRY_JUICE;

    public static final ImmutableList<JuiceItem> JUICES;

    static {
        THERMOMETER_ITEM = register("thermometer", new Thermometer(new Item.Settings().group(ItemGroup.MISC)));
        CANTEEN_ITEM = register("canteen", new CanteenItem(newSettings()));

        ImmutableList.Builder<JuiceItem> juices = ImmutableList.builder();
        APPLE_JUICE = register("juice_apple", JuiceType.APPLE, juices);
        BEETROOT_JUICE = register("juice_beetroot", JuiceType.BEETROOT, juices);
        CACTUS_JUICE = register("juice_cactus", JuiceType.CACTUS, juices);
        SWEET_BERRY_JUICE = register("juice_sweet_berry", JuiceType.SWEET_BERRY, juices);
        JUICES = juices.build();
    }

    private static Item.Settings newSettings() {
        return new Item.Settings().group(ItemGroup.MISC);
    }

    private static <T extends Item> T register(String name, T item) {
        return Registry.register(ITEM, ToughEnough.identifier(name), item);
    }

    private static JuiceItem register(String name, JuiceType type, ImmutableList.Builder<JuiceItem> juices) {
        JuiceItem juice = new JuiceItem(newSettings(), type);
        juices.add(juice);
        return register(name, juice);
    }

    public static void register() {
        // keep for class initialisation (call from initializer)
    }

}
