package io.github.louis9902.toughenough.item;

import net.minecraft.item.ItemStack;

public interface DamageBypassItem {

    int getDamage(ItemStack stack);

    int getMaxDamage(ItemStack stack);

    boolean isDamaged(ItemStack stack);
}
