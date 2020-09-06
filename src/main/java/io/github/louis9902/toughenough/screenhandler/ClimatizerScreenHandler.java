package io.github.louis9902.toughenough.screenhandler;

import io.github.louis9902.toughenough.block.Climatizer;
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
    private final PropertyDelegate propertyDelegate;

    public ClimatizerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(1), new ArrayPropertyDelegate(3));
    }

    public ClimatizerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate delegate) {
        super(ToughEnoughScreenHandlers.CLIMATIZER_SCREEN_HANDLER, syncId);
        checkSize(inventory, 1);
        this.inventory = inventory;
        this.propertyDelegate = delegate;
        this.addProperties(propertyDelegate);

        int m;
        int l;

        //The fuel slot, we use ClimateSlot here to prevent non compatible Items from being inserted
        //The methods CanInsertIntoSlot from ScreenHandler did not work for me
        this.addSlot(new ClimateSlot(inventory, 0, 80, 30));

        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    //Todo add proper implementation for this
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

    @Environment(EnvType.CLIENT)
    public boolean isBurning() {
        return propertyDelegate.get(0) > 0;
    }

    @Environment(EnvType.CLIENT)
    public float getFuelProgress() {
        if (propertyDelegate.get(1) == 0) return 0.0f;
        return (float) (propertyDelegate.get(1) - propertyDelegate.get(0)) / propertyDelegate.get(1);
    }

    @Environment(EnvType.CLIENT)
    public Climatizer.Action getOperatingMode() {
        return Climatizer.Action.values()[propertyDelegate.get(2)];
    }
}
