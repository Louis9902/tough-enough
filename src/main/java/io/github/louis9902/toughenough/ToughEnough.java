package io.github.louis9902.toughenough;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class ToughEnough implements ModInitializer {

    public static Identifier identifier(String path) {
        return new Identifier("tough_enough", path);
    }

    @Override
    public void onInitialize() {

    }
}
