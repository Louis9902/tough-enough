package io.github.louis9902.toughenough.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.louis9902.toughenough.api.temperature.TemperatureManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.DoubleUnaryOperator;

import static io.github.louis9902.toughenough.ToughEnoughComponents.TEMPERATURE_MANAGER;
import static io.github.louis9902.toughenough.temperature.HeatManagerConstants.MAX_TARGET;
import static io.github.louis9902.toughenough.temperature.HeatManagerConstants.MIN_TARGET;

public final class TemperatureHudRenderer extends DrawableHelper {
    //The functions for modelling our color transitions in relation to current heat
    static DoubleUnaryOperator coldRed = linearFunc(0, 0, 0.5f, 1f);
    static DoubleUnaryOperator coldGreen = linearFunc(0, 0.5f, 0.5f, 1);
    static DoubleUnaryOperator hotGreen = linearFunc(0.5f, 1, 1, 0.5f);
    static DoubleUnaryOperator hotBlue = linearFunc(0.5f, 1, 1, 0);

    private final MinecraftClient client;

    private TemperatureHudRenderer() {
        client = MinecraftClient.getInstance();
    }

    public static void register() {
        TemperatureHudRenderer renderer = new TemperatureHudRenderer();
        HudRenderCallback.EVENT.register(renderer::render);
    }

    //returns linear function going trough two points
    private static DoubleUnaryOperator linearFunc(float x1, float y1, float x2, float y2) {
        if (x1 == x2) return (x) -> (y1);
        return x -> ((y2 - y1) / (x2 - x1)) * (x - x1) + y1;
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

            int x = width / 2 - 8;
            int y = height - 48;

            PlayerEntity player = HudRenderHelper.getCameraPlayer();
            if (player != null) {
                TemperatureManager heatMan = TEMPERATURE_MANAGER.get(player);
                float heatPerc = (heatMan.getTemperature() - MIN_TARGET) / (float) (MAX_TARGET - MIN_TARGET);
                int xoff = 1;

                if (heatPerc >= 0.5) {
                    //choose flame texture
                    if (heatPerc > 0.90) {
                        xoff += 17;
                    } else
                        RenderSystem.color3f(1, (float) hotGreen.applyAsDouble(heatPerc), (float) hotBlue.applyAsDouble(heatPerc));
                } else {
                    //choose snowflake texture
                    if (heatPerc < 0.10) {
                        xoff += 17 * 2;
                    } else
                        RenderSystem.color3f((float) coldRed.applyAsDouble(heatPerc), (float) coldGreen.applyAsDouble(heatPerc), 1);
                }

                drawTexture(matrices, x, y, xoff, 11, 17, 17);
                drawTexture(matrices, x, y, xoff, 28, 17, 17);
            }
        }
        RenderSystem.disableBlend();
        matrices.pop();

    }

}
