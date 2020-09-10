package io.github.minnecraeft.toughenough.screen;

import io.github.minnecraeft.toughenough.init.ToughEnoughTags;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ClimateSlot extends Slot {

    public ClimateSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.getItem().isIn(ToughEnoughTags.CLIMATIZER_ITEMS);
    }
}
