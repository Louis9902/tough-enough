package io.github.louis9902.toughenough.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.louis9902.toughenough.ToughEnough;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import static io.github.louis9902.toughenough.ToughEnoughComponents.THIRSTY;
import static io.github.louis9902.toughenough.init.ToughEnoughStatusEffects.THIRST;

public final class ThirstHudRenderer extends DrawableHelper {

    private static final Identifier STATUS_BAR_ICONS = ToughEnough.identifier("textures/gui/statusbar.png");

    private final MinecraftClient client;

    public ThirstHudRenderer() {
        client = MinecraftClient.getInstance();
    }

    public static void register() {
        ThirstHudRenderer renderer = new ThirstHudRenderer();
        HudRenderCallback.EVENT.register(renderer::render);
    }

    private void render(MatrixStack matrices, float delta) {
        if (client.options.hudHidden || !client.interactionManager.hasStatusBars()) return;

        matrices.push();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        {
            client.getTextureManager().bindTexture(STATUS_BAR_ICONS);

            PlayerEntity player = getCameraPlayer();
            if (player != null) {
                int width = client.getWindow().getScaledWidth();
                int height = client.getWindow().getScaledHeight();

                int x = width / 2 + 10;

                int thirst = THIRSTY.get(player).getThirst();

                if (player.hasStatusEffect(THIRST)) {
                    for (int i = 0; i < 10; i++) {
                        drawTexture(matrices, x + i * 8, height - 49, 18, 0, 9, 9);
                    }
                } else {
                    for (int i = 0; i < 10; i++) {
                        drawTexture(matrices, x + i * 8, height - 49, 0, 0, 9, 9);
                    }
                }

            }
        }
        RenderSystem.disableBlend();
        matrices.pop();
    }

    private static PlayerEntity getCameraPlayer() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.getCameraEntity() instanceof PlayerEntity ? (PlayerEntity) client.getCameraEntity() : null;
    }

}
