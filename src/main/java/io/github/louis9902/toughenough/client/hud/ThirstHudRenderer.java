package io.github.louis9902.toughenough.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.louis9902.toughenough.ToughEnough;
import io.github.louis9902.toughenough.components.ThirstManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

import java.util.Random;

import static io.github.louis9902.toughenough.ToughEnoughComponents.HEATY;
import static io.github.louis9902.toughenough.ToughEnoughComponents.THIRSTY;
import static io.github.louis9902.toughenough.init.ToughEnoughStatusEffects.THIRST;

public final class ThirstHudRenderer extends DrawableHelper {

    private static final Identifier STATUS_BAR_ICONS = ToughEnough.identifier("textures/gui/statusbar.png");

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
        PlayerEntity p = getCameraPlayer();
        drawCenteredString(matrices, client.textRenderer, "Thirst: " + THIRSTY.get(p).getThirst(), 100, 100, 0);
        drawCenteredString(matrices, client.textRenderer, "Hydration: " + THIRSTY.get(p).getHydration(), 100, 110, 0);
        drawCenteredString(matrices, client.textRenderer, "Exhaustion: " + THIRSTY.get(p).getExhaustion(), 100, 120, 0);
        drawCenteredString(matrices, client.textRenderer, "Temperature: " + HEATY.get(p).getTemperature(), 100, 130, 0);
        drawCenteredString(matrices, client.textRenderer, "Target: " + HEATY.get(p).getTarget(), 100, 140, 0);
        drawCenteredString(matrices, client.textRenderer, "Rate: " + HEATY.get(p).getRate(), 100, 150, 0);
        matrices.pop();

        matrices.push();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        {
            client.getTextureManager().bindTexture(STATUS_BAR_ICONS);

            PlayerEntity player = getCameraPlayer();
            if (player != null) {
                ThirstManager manager = THIRSTY.get(player);

                int width = client.getWindow().getScaledWidth();
                int height = client.getWindow().getScaledHeight();

                // -- Thirst --

                int x = width / 2 + 9;
                int y = height - 49;

                int offset = player.hasStatusEffect(THIRST) ? 9 : 0;

                int thirst = manager.getThirst();
                float hydration = manager.getHydration();

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
