package io.github.louis9902.toughenough.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.louis9902.toughenough.ToughEnough;
import io.github.louis9902.toughenough.block.ClimatizerBlock;
import io.github.louis9902.toughenough.screenhandler.ClimatizerScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ClimatizerScreen extends HandledScreen<ClimatizerScreenHandler> {
    private static final Identifier TEXTURE = ToughEnough.identifier("textures/gui/climatizer.png");

    public ClimatizerScreen(ClimatizerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        client.getTextureManager().bindTexture(TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);

        //We render the progress bar based on the operating mode. If the block is OFF we don't render anything
        ClimatizerBlock.Action operatingMode = handler.getOperatingMode();
        switch (operatingMode) {
            case COOL:
                this.drawTexture(matrices, x + 79, y + 50, 176, 0, Math.round(18 * handler.getFuelProgress()), 4);
                break;
            case HEAT:
                this.drawTexture(matrices, x + 79, y + 50, 176, 5, Math.round(18 * handler.getFuelProgress()), 4);
                break;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}
