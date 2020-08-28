package io.github.louis9902.toughenough.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.louis9902.toughenough.api.thirst.ThirstManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Random;

import static io.github.louis9902.toughenough.ToughEnoughComponents.*;
import static io.github.louis9902.toughenough.init.ToughEnoughStatusEffects.THIRST;

public final class ThirstHudRenderer extends DrawableHelper {

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
        if (HudRenderHelper.skipStatusElements()) return;

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


                ThirstManager manager = THIRST_MANAGER.get(player);

                x = width / 2 + 9;
                y = height - 49;

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

            }
        }
        RenderSystem.disableBlend();
        matrices.pop();
    }

}
