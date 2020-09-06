package io.github.louis9902.toughenough.init;

import io.github.louis9902.toughenough.ToughEnough;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

public final class ToughEnoughTags {

    public static final Tag<Item> JUICES;
    public static final Tag<Item> COOLING;
    public static final Tag<Item> HEATING;
    public static final Tag<Item> CLIMATIZER_ITEMS;

    static {
        JUICES = TagRegistry.item(ToughEnough.identifier("juices"));
        COOLING = TagRegistry.item(ToughEnough.identifier("cooling"));
        HEATING = TagRegistry.item(ToughEnough.identifier("heating"));
        CLIMATIZER_ITEMS = TagRegistry.item(ToughEnough.identifier("climatizer_items"));
    }

    public static void register() {
        // keep for class initialisation (call from initializer)
    }

}
