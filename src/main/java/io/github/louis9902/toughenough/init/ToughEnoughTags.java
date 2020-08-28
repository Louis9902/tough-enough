package io.github.louis9902.toughenough.init;

import io.github.louis9902.toughenough.ToughEnough;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

public final class ToughEnoughTags {

    public static final Tag<Item> JUICES;

    static {
        JUICES = TagRegistry.item(ToughEnough.identifier("juices"));
    }

    public static void register() {
        // keep for class initialisation (call from initializer)
    }

}
