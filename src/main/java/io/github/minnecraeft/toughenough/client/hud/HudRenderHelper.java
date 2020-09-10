package io.github.minnecraeft.toughenough.client.hud;

import io.github.minnecraeft.toughenough.ToughEnough;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class HudRenderHelper {

    public static final Identifier STATUS_BAR_ICONS = ToughEnough.identifier("textures/gui/statusbar.png");

    public static boolean skipStatusElements() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.options.hudHidden || (client.interactionManager != null && !client.interactionManager.hasStatusBars());
    }

    public static @Nullable PlayerEntity getCameraPlayer() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.getCameraEntity() instanceof PlayerEntity ? (PlayerEntity) client.getCameraEntity() : null;
    }

}
