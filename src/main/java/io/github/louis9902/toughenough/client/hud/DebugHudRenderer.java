package io.github.louis9902.toughenough.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.louis9902.toughenough.ToughEnoughComponents;
import io.github.louis9902.toughenough.api.temperature.TemperatureManager;
import io.github.louis9902.toughenough.api.thirst.ThirstManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;

public final class DebugHudRenderer extends DrawableHelper {

    private static final int FONT_COLOR = 255 << 16 | 255 << 8 | 255;

    private final MinecraftClient client;

    private DebugHudRenderer() {
        client = MinecraftClient.getInstance();
    }

    public static void register() {
        DebugHudRenderer renderer = new DebugHudRenderer();
        HudRenderCallback.EVENT.register(renderer::render);
    }

    private void render(MatrixStack matrices, float delta) {
        if (HudRenderHelper.skipStatusElements())
            return;

        matrices.push();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        {
            PlayerEntity player = HudRenderHelper.getCameraPlayer();

            TemperatureManager managerHeat = ToughEnoughComponents.TEMPERATURE_MANAGER.get(player);

            if (managerHeat.getDebug()) {
                drawStringWithShadow(matrices, client.textRenderer, "Rate: " + managerHeat.getRate(), 0, 0, FONT_COLOR);
                drawStringWithShadow(matrices, client.textRenderer, "Rate Modifiers: ", 0, 10, FONT_COLOR);
                int counter = 0;
                for (Map.Entry<String, String> a : managerHeat.getRateMonitor()) {
                    drawStringWithShadow(matrices, client.textRenderer, a.getKey() + ": " + a.getValue(), 0, 20 + 10 * counter, FONT_COLOR);
                    counter++;
                }

                drawStringWithShadow(matrices, client.textRenderer, "Target: " + managerHeat.getTarget(), 100, 0, FONT_COLOR);
                drawStringWithShadow(matrices, client.textRenderer, "Target Modifiers: ", 100, 10, FONT_COLOR);
                counter = 0;
                for (Map.Entry<String, String> a : managerHeat.getTargetMonitor()) {
                    drawStringWithShadow(matrices, client.textRenderer, a.getKey() + ": " + a.getValue(), 100, 20 + 10 * counter, FONT_COLOR);
                    counter++;
                }
            }

            ThirstManager managerThirst = ToughEnoughComponents.THIRST_MANAGER.get(player);

            if (managerThirst.getDebug()) {
                drawStringWithShadow(matrices, client.textRenderer, "Thirst: " + managerThirst.getThirst(), 200, 0, FONT_COLOR);
                drawStringWithShadow(matrices, client.textRenderer, "Hydration: " + managerThirst.getHydration(), 200, 10, FONT_COLOR);
                drawStringWithShadow(matrices, client.textRenderer, "Exhaustion: " + managerThirst.getExhaustion(), 200, 20, FONT_COLOR);
            }
        }
        RenderSystem.disableBlend();
        matrices.pop();

    }

}
