package io.github.louis9902.toughenough.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.louis9902.toughenough.ToughEnough;
import io.github.louis9902.toughenough.components.TemperatureManager;
import io.github.louis9902.toughenough.components.ThirstManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Random;

import static io.github.louis9902.toughenough.ToughEnoughComponents.HEATY;
import static io.github.louis9902.toughenough.ToughEnoughComponents.THIRSTY;
import static io.github.louis9902.toughenough.init.ToughEnoughStatusEffects.THIRST;

public final class ThirstHudRenderer extends DrawableHelper {

    private static final Identifier STATUS_BAR_ICONS = ToughEnough.identifier("textures/gui/statusbar.png");
    private static final int FONT_COLOR = 255 << 16 | 255 << 8 | 255;

    private final MinecraftClient client;
    private final Random random;

    private ThirstHudRenderer() {
        client = MinecraftClient.getInstance();
        random = new Random();
    }

    public static void register() {
        ThirstHudRenderer renderer = new ThirstHudRenderer();
        HudRenderCallback.EVENT.register(renderer::render);
    }

    private void render(MatrixStack matrices, float delta) {
        if (client.options.hudHidden || (client.interactionManager != null && !client.interactionManager.hasStatusBars()))
            return;

        matrices.push();
        PlayerEntity player = getCameraPlayer();

        ThirstManager thirstManager = THIRSTY.get(player);
        TemperatureManager tempManager = HEATY.get(player);

        if (tempManager.getDebug()) {
            drawStringWithShadow(matrices, client.textRenderer, "Rate: " + tempManager.getRate(), 0, 0, FONT_COLOR);
            drawStringWithShadow(matrices, client.textRenderer, "Rate Modifiers: ", 0, 10, FONT_COLOR);
            int counter = 0;
            for (Map.Entry<String, String> a : tempManager.getRateMonitor()) {
                drawStringWithShadow(matrices, client.textRenderer, a.getKey() + ": " + a.getValue(), 0, 20 + 10 * counter, FONT_COLOR);
                counter++;
            }

            drawStringWithShadow(matrices, client.textRenderer, "Target: " + tempManager.getTarget(), 100, 0, FONT_COLOR);
            drawStringWithShadow(matrices, client.textRenderer, "Target Modifiers: ", 100, 10, FONT_COLOR);
            counter = 0;
            for (Map.Entry<String, String> a : tempManager.getTargetMonitor()) {
                drawStringWithShadow(matrices, client.textRenderer, a.getKey() + ": " + a.getValue(), 100, 20 + 10 * counter, FONT_COLOR);
                counter++;
            }
        }
        if (thirstManager.getDebug()) {
            drawStringWithShadow(matrices, client.textRenderer, "Thirst: " + THIRSTY.get(player).getThirst(), 200, 0, FONT_COLOR);
            drawStringWithShadow(matrices, client.textRenderer, "Hydration: " + THIRSTY.get(player).getHydration(), 200, 10, FONT_COLOR);
            drawStringWithShadow(matrices, client.textRenderer, "Exhaustion: " + THIRSTY.get(player).getExhaustion(), 200, 20, FONT_COLOR);
        }
        matrices.pop();

        matrices.push();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        {
            client.getTextureManager().bindTexture(STATUS_BAR_ICONS);

            if (player != null) {

                int width = client.getWindow().getScaledWidth();
                int height = client.getWindow().getScaledHeight();

                // -- Thirst --

                int x = width / 2 + 9;
                int y = height - 49;

                int offset = player.hasStatusEffect(THIRST) ? 9 : 0;

                int thirst = thirstManager.getThirst();
                float hydration = thirstManager.getHydration();

                // drawing texture starting from the back
                x += 72;

                boolean hops = false;
                if (hydration <= 0.0F && player.world.getTime() % (thirst * 3 + 1) == 0) {
                    hops = true;
                }

                // drawing the full drops
                int fullDrops = thirst / 2;
                for (int i = 0; i < fullDrops; i++) {
                    int yo = hops ? (y + random.nextInt(3) - 1) : y;
                    drawTexture(matrices, x - i * 8, yo, offset, 0, 9, 9);
                    drawTexture(matrices, x - i * 8, yo, offset + 18, 0, 9, 9);
                }

                // drawing the half drops
                int halfDrops = thirst % 2;
                if (halfDrops > 0) {
                    int yo = hops ? (y + random.nextInt(3) - 1) : y;
                    drawTexture(matrices, x - (fullDrops * 8), yo, offset, 0, 9, 9);
                    drawTexture(matrices, x - (fullDrops * 8), yo, offset + 36, 0, 9, 9);
                }

                // drawing the rest of the drops shadows
                int drops = fullDrops + halfDrops;
                if (drops < 10) {
                    for (; drops < 10; drops++) {
                        int yo = hops ? (y + random.nextInt(3) - 1) : y;
                        drawTexture(matrices, x - (drops * 8), yo, offset, 0, 9, 9);
                    }
                }

                // -- Heat --

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

    private static PlayerEntity getCameraPlayer() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.getCameraEntity() instanceof PlayerEntity ? (PlayerEntity) client.getCameraEntity() : null;
    }

}
