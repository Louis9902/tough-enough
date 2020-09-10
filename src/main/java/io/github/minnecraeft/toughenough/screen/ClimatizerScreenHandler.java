package io.github.minnecraeft.toughenough.screen;

import io.github.minnecraeft.toughenough.block.ClimatizerBlock;
import io.github.minnecraeft.toughenough.init.ToughEnoughScreenHandlers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class ClimatizerScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final PropertyDelegate delegate;

    public ClimatizerScreenHandler(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new SimpleInventory(1), new ArrayPropertyDelegate(3));
    }

    public ClimatizerScreenHandler(int id, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate delegate) {
        super(ToughEnoughScreenHandlers.CLIMATIZER_SCREEN_HANDLER, id);
        checkSize(inventory, 1);
        this.inventory = inventory;
        this.delegate = delegate;
        this.addProperties(this.delegate);

        int m;
        int l;

        // The fuel slot, we use ClimateSlot here to prevent non compatible Items from being inserted
        // The methods CanInsertIntoSlot from ScreenHandler did not work for me
        this.addSlot(new ClimateSlot(inventory, 0, 80, 30));

        // The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; l++) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        // The player hot bar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    //This is called when the player shift clicks a slot in the container or his own inventory
    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            //The ItemStack that was shift clicked on
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            //Case 1: Player shift clicked on slot in container -> insert into player inventory
            if (invSlot < this.inventory.size()) {
                //return empty item stack if insertion fails
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            //Case 2: Player shift clicked on slot in his inventory -> insert into container
            else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                //return empty item stack if insertion fails
                return ItemStack.EMPTY;
            }

            //Case 1: The clicked on stack was completely transferred
            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            }
            //Case 2: It was not completely consumed
            else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    @Environment(EnvType.CLIENT)
    public boolean isBurning() {
        return delegate.get(0) > 0;
    }

    @Environment(EnvType.CLIENT)
    public float getFuelProgress() {
        int totalBurnTime = delegate.get(1);
        int remainingBurnTime = delegate.get(0);
        return totalBurnTime != 0 ? (float) (totalBurnTime - remainingBurnTime) / totalBurnTime : 0.0f;
    }

    @Environment(EnvType.CLIENT)
    public ClimatizerBlock.Action getOperatingMode() {
        return ClimatizerBlock.Action.values()[delegate.get(2)];
    }
}
