package io.github.minnecraeft.toughenough;

import io.github.minnecraeft.toughenough.init.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class ToughEnough implements ModInitializer {

    public static final String ID = "tough_enough";

    public static Identifier identifier(String name) {
        return new Identifier(ID, name);
    }

    public static Identifier minecraft(String name) {
        return new Identifier("minecraft", name);
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
        ToughEnoughCommands.register();
    }
}
