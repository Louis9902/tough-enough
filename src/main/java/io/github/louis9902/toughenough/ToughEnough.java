package io.github.louis9902.toughenough;

import io.github.louis9902.toughenough.init.Gameplay;
import io.github.louis9902.toughenough.init.ToughEnoughItems;
import io.github.louis9902.toughenough.init.ToughEnoughRegistries;
import io.github.louis9902.toughenough.init.ToughEnoughStatusEffects;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;


public class ToughEnough implements ModInitializer {

    public static final String ID = "tough_enough";

    @Override
    public void onInitialize() {
        Gameplay.register();
        ToughEnoughRegistries.register();
        ToughEnoughItems.register();
        ToughEnoughStatusEffects.register();
    }

    public static Identifier identifier(String name) {
        return new Identifier(ID, name);
    }
}
