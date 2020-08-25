package io.github.louis9902.toughenough.mixin;

import io.github.louis9902.toughenough.item.DamageBypassItem;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Redirect(
            method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isDamaged()Z"
            ))
    private boolean isDamaged(ItemStack stack) {
        if (stack.getItem() instanceof DamageBypassItem) {
            return ((DamageBypassItem) stack.getItem()).isDamaged(stack);
        }
        return stack.isDamaged();
    }

    @Redirect(
            method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getDamage()I"
            ))
    private int getDamage(ItemStack stack) {
        if (stack.getItem() instanceof DamageBypassItem) {
            return ((DamageBypassItem) stack.getItem()).getDamage(stack);
        }
        return stack.getDamage();
    }

    @Redirect(
            method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I"
            ))
    private int getMaxDamage(ItemStack stack) {
        if (stack.getItem() instanceof DamageBypassItem) {
            return ((DamageBypassItem) stack.getItem()).getMaxDamage(stack);
        }
        return stack.getMaxDamage();
    }

}
