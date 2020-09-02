package io.github.louis9902.toughenough.init;

import io.github.louis9902.toughenough.ToughEnough;
import io.github.louis9902.toughenough.screenhandler.ClimatizerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;

public class ToughEnoughScreenHandlers {
    public static final ScreenHandlerType<ClimatizerScreenHandler> CLIMATIZER_SCREEN_HANDLER;

    static {
        CLIMATIZER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(ToughEnough.identifier("climatizer"), ClimatizerScreenHandler::new);
    }

    public static void register() {
        // keep for class initialisation (call from initializer)
    }
}
