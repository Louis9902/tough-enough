package io.github.louis9902.toughenough.screenhandler;

import io.github.louis9902.toughenough.block.ClimatizerBlock;
import io.github.louis9902.toughenough.init.ToughEnoughScreenHandlers;
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

    // TODO: add proper implementation for this
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
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
