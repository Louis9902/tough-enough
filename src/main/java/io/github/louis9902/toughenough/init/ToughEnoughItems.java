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

    public static final ImmutableList<JuiceItem> JUICES;

    static {
        CANTEEN = RegistryHelpers.register("canteen", new CanteenItem(newSettings()));
        THERMOMETER = RegistryHelpers.register("thermometer", new ThermometerItem(newSettings()));

        JUICES = ImmutableList.of(
                (APPLE_JUICE = RegistryHelpers.register("juice_apple", new JuiceItem(newSettings(), JuiceType.APPLE))),
                (BEETROOT_JUICE = RegistryHelpers.register("juice_beetroot", new JuiceItem(newSettings(), JuiceType.BEETROOT))),
                (CACTUS_JUICE = RegistryHelpers.register("juice_cactus", new JuiceItem(newSettings(), JuiceType.CACTUS))),
                (CARROT_JUICE = RegistryHelpers.register("juice_carrot", new JuiceItem(newSettings(), JuiceType.CARROT))),
                (MELON_JUICE = RegistryHelpers.register("juice_melon", new JuiceItem(newSettings(), JuiceType.MELON))),
                (PUMPKIN_JUICE = RegistryHelpers.register("juice_pumpkin", new JuiceItem(newSettings(), JuiceType.PUMPKIN))),
                (SWEET_BERRY_JUICE = RegistryHelpers.register("juice_sweet_berry", new JuiceItem(newSettings(), JuiceType.SWEET_BERRY)))
        );

        GROUP = FabricItemGroupBuilder.build(ToughEnough.identifier("general"), () -> new ItemStack(CANTEEN));
    }

    private static Item.Settings newSettings() {
        return new Item.Settings().group(GROUP);
    }


    public static void register() {
        // keep for class initialisation (call from initializer)
    }

}
