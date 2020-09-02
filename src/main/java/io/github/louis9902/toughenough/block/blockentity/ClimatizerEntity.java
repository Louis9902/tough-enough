package io.github.louis9902.toughenough.block.blockentity;

import io.github.louis9902.toughenough.block.misc.FuckYouInv;
import io.github.louis9902.toughenough.init.ToughEnoughBlockEntities;
import io.github.louis9902.toughenough.screenhandler.ClimatizerScreenHandler;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;

//Todo consider sidedInventory for this
public class ClimatizerEntity extends LockableContainerBlockEntity implements Tickable, FuckYouInv, NamedScreenHandlerFactory {
    private static final int INVENTORY_SIZE = 1;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private int burnTime;
    private int fuelTime;
    PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
                case 1:
                    return burnTime;
                case 2:
                    return fuelTime;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 1:
                    burnTime = value;
                case 2:
                    fuelTime = value;
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public ClimatizerEntity() {
        super(ToughEnoughBlockEntities.CLIMATIZER_ENTITY_TYPE);
    }

    @Override
    public void tick() {
        if (inventory.get(0) != ItemStack.EMPTY)
            System.out.println("ClimatizerEntity.tick FUEL IN ME");
    }


    @Override
    protected Text getContainerName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new ClimatizerScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> stacks() {
        return inventory;
    }
}
