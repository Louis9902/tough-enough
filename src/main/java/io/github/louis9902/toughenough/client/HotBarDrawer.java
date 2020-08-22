package io.github.louis9902.toughenough.client;

import io.github.louis9902.toughenough.MyComponents;
import io.github.louis9902.toughenough.ToughEnough;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class HotBarDrawer extends DrawableHelper {
    private static final Identifier HOTBAR_TEXTURE = ToughEnough.identifier("textures/gui/statusbar.png");

    public HotBarDrawer() {

        HudRenderCallback.EVENT.register(((matrixStack, v) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (!client.options.hudHidden && client.interactionManager.hasStatusBars()) {
                //RenderSystem.enableBlend();
                //RenderSystem.enableAlphaTest();
                //RenderSystem.defaultBlendFunc();
                //RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                matrixStack.push();
                client.getTextureManager().bindTexture(HOTBAR_TEXTURE);

                int scaledWidth = client.getWindow().getScaledWidth();
                int scaledHeight = client.getWindow().getScaledHeight();

                int barX = scaledWidth / 2 + 10;

                PlayerEntity player = getCameraPlayer();

                if (player != null) {
                    //TODO: for other players than the client player thirst is not synced yet
                    //TODO: dont hardcode max thirst level
                    int thirst = MyComponents.THIRSTY.get(player).getThirst();
                    int maxThirst = 20;

                    if (player.hasStatusEffect(ToughEnough.THIRST_EFFECT)) {
                        for (int i = 0; i < 10; ++i) {
                            drawTexture(matrixStack, barX + i * 8, scaledHeight - 49, 18, 0, 9, 9);
                        }
                    } else {
                        for (int i = 0; i < 10; ++i) {
                            drawTexture(matrixStack, barX + i * 8, scaledHeight - 49, 0, 0, 9, 9);
                        }
                    }


                }
                //RenderSystem.disableBlend();
                //RenderSystem.disableAlphaTest();
                matrixStack.pop();
            }

        }));
    }

    private PlayerEntity getCameraPlayer() {
        return MinecraftClient.getInstance().getCameraEntity() instanceof PlayerEntity ? (PlayerEntity) MinecraftClient.getInstance().getCameraEntity() : null;
    }

}
