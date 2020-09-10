package io.github.minnecraeft.toughenough.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.minnecraeft.toughenough.ToughEnoughComponents;
import io.github.minnecraeft.toughenough.api.temperature.TemperatureConstants;
import io.github.minnecraeft.toughenough.api.temperature.TemperatureManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.DoubleUnaryOperator;

public final class TemperatureHudRenderer extends DrawableHelper {

    // The functions for modelling our color transitions in relation to current heat
    private static final DoubleUnaryOperator COLOR_FUNC_COLD_RED = funcLinear(0, 0, 0.5f, 1f);
    private static final DoubleUnaryOperator COLOR_FUNC_COLD_GREEN = funcLinear(0, 0.5f, 0.5f, 1);
    private static final DoubleUnaryOperator COLOR_FUNC_HOT_GREEN = funcLinear(0.5f, 1, 1, 0.5f);
    private static final DoubleUnaryOperator COLOR_FUNC_HIT_BLUE = funcLinear(0.5f, 1, 1, 0);

    private final MinecraftClient client;

    private TemperatureHudRenderer() {
        client = MinecraftClient.getInstance();
    }

    public static void register() {
        TemperatureHudRenderer renderer = new TemperatureHudRenderer();
        HudRenderCallback.EVENT.register(renderer::render);
    }

    /**
     * Calculate a function going through two points for color calculations.
     *
     * @return a linear function going trough two points
     */
    private static DoubleUnaryOperator funcLinear(float x1, float y1, float x2, float y2) {
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
            int y = height - 53;

            PlayerEntity player = HudRenderHelper.getCameraPlayer();
            if (player != null) {
                TemperatureManager manager = ToughEnoughComponents.TEMPERATURE_MANAGER.get(player);
                float heat = (manager.getTemperature() - TemperatureConstants.MIN_TEMPERATURE_TARGET) / (float) (TemperatureConstants.MAX_TEMPERATURE_TARGET - TemperatureConstants.MIN_TEMPERATURE_TARGET);

                int xoff = 1;

                if (heat >= 0.5) {
                    // choose flame texture
                    if (heat > 0.90) {
                        xoff += 17;
                    } else {
                        RenderSystem.color3f(1, (float) COLOR_FUNC_HOT_GREEN.applyAsDouble(heat), (float) COLOR_FUNC_HIT_BLUE.applyAsDouble(heat));
                    }
                } else {
                    // choose snowflake texture
                    if (heat < 0.10) {
                        xoff += 17 * 2;
                    } else {
                        RenderSystem.color3f((float) COLOR_FUNC_COLD_RED.applyAsDouble(heat), (float) COLOR_FUNC_COLD_GREEN.applyAsDouble(heat), 1);
                    }
                }

                drawTexture(matrices, x, y, xoff, 11, 17, 17);
                drawTexture(matrices, x, y, xoff, 28, 17, 17);
            }
        }
        RenderSystem.disableBlend();
        matrices.pop();

    }

}
