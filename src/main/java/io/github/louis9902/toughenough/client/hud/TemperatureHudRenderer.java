package io.github.louis9902.toughenough.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;

public final class TemperatureHudRenderer extends DrawableHelper {

    private final MinecraftClient client;

    private TemperatureHudRenderer() {
        client = MinecraftClient.getInstance();
    }

    public static void register() {
        TemperatureHudRenderer renderer = new TemperatureHudRenderer();
        HudRenderCallback.EVENT.register(renderer::render);
    }

    private void render(MatrixStack matrices, float delta) {
        if (HudRenderHelper.skipStatusElements())
            return;

        matrices.push();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        {
            client.getTextureManager().bindTexture(HudRenderHelper.STATUS_BAR_ICONS);

            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();

            int x, y;

            PlayerEntity player = HudRenderHelper.getCameraPlayer();
            if (player != null) {

                Arm arm = player.getMainArm();

                // 91 offset from center - 6 padding ( - 8 texture width when arm right )
                x = arm == Arm.RIGHT ? (width / 2 - 91 - 6 - 8) : (width / 2 + 91 + 6);
                y = height - 19;

                int head = 3;
                drawTexture(matrices, x, y, head * 9, 10, 8, 17);
            }
        }
        RenderSystem.disableBlend();
        matrices.pop();

    }

}
