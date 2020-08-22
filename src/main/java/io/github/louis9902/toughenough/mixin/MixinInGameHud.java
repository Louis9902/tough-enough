package io.github.louis9902.toughenough.mixin;

import io.github.louis9902.toughenough.init.Gameplay;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud extends DrawableHelper {

    /*
     * The redirect mixins are used to redirect the texture draw calls for the air bubbles.
     * Because our mod renders the thirst at the position of the air bubbles when they appear
     * we are shifting them up by 10 pixels to make space for the thirst status bar.
     *
     * Side note:
     *      The slice is setting the injection counter for the drawTexture call to 0 and starting from
     *      getMaxAir so we dont depend on the actual count of the drawTexture calls in the renderStatusBars
     *      method
     */

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @Redirect(method = "renderStatusBars",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getMaxAir()I")
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
                    ordinal = 0
            )
    )
    private void renderFullTexture(InGameHud helper, MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        boolean enable = Gameplay.isThirstEnabled(getCameraPlayer().world);
        helper.drawTexture(matrices, x, y - (enable ? 10 : 0), u, v, width, height);
    }

    @Redirect(method = "renderStatusBars",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getMaxAir()I")
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
                    ordinal = 1
            )
    )
    private void renderPartialTexture(InGameHud helper, MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        boolean enable = Gameplay.isThirstEnabled(getCameraPlayer().world);
        helper.drawTexture(matrices, x, y - (enable ? 10 : 0), u, v, width, height);
    }

}
