package io.github.louis9902.toughenough.client;

import io.github.louis9902.toughenough.MyComponents;
import io.github.louis9902.toughenough.ToughEnough;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class HotBarDrawer extends DrawableHelper {
    private static final Identifier HOTBAR_TEXTURE = ToughEnough.identifier("textures/misc/statusbar.png");

    public HotBarDrawer() {

        HudRenderCallback.EVENT.register(((matrixStack, v) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (!client.options.hudHidden) {
                matrixStack.push();
                client.getTextureManager().bindTexture(HOTBAR_TEXTURE);

                int scaledWidth = client.getWindow().getScaledWidth();
                int scaledHeight = client.getWindow().getScaledHeight();

                int barX = scaledWidth / 2 + 91;

                PlayerEntity player = getCameraPlayer();

                if (player != null) {
                    //TODO: for other players than the client player thirst is not synced yet
                    //TODO: dont hardcode max thirst level
                    int thirst = MyComponents.THIRSTY.get(player).getThirst();
                    int maxThirst = 20;

                    for (int i = 0; i < 10; ++i) {
                        drawTexture(matrixStack, barX + i * 9, scaledHeight - 10, 0, 0, 9, 9);
                    }

                }
                matrixStack.pop();
            }

        }));
    }

    private PlayerEntity getCameraPlayer() {
        return MinecraftClient.getInstance().getCameraEntity() instanceof PlayerEntity ? (PlayerEntity) MinecraftClient.getInstance().getCameraEntity() : null;
    }

}
