package io.github.louis9902.toughenough;

import io.github.louis9902.toughenough.init.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;


public class ToughEnough implements ModInitializer {

    public static final String ID = "tough_enough";

    @Override
    public void onInitialize() {
        ToughEnoughItems.register();
        ToughEnoughRegistries.register();
        ToughEnoughStatusEffects.register();
        ToughEnoughTags.register();
    }

    public static Identifier identifier(String name) {
        return new Identifier(ID, name);
    }
}
