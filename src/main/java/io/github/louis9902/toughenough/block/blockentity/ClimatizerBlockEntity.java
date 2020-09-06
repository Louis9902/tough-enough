package io.github.louis9902.toughenough.block.blockentity;

import io.github.louis9902.toughenough.block.ClimatizerBlock;
import io.github.louis9902.toughenough.block.misc.FuckYouInv;
import io.github.louis9902.toughenough.init.ToughEnoughBlockEntities;
import io.github.louis9902.toughenough.init.ToughEnoughItems;
import io.github.louis9902.toughenough.screenhandler.ClimatizerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Pair;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashMap;

// TODO: consider sidedInventory for this
public class ClimatizerBlockEntity extends LockableContainerBlockEntity implements Tickable, FuckYouInv, NamedScreenHandlerFactory {

    private static final HashMap<Item, Pair<Integer, FuelType>> FUEL_ITEMS = new HashMap<>();

    static {
        FUEL_ITEMS.put(ToughEnoughItems.ICE_SHARD, new Pair<>(400, FuelType.COOLING));
        FUEL_ITEMS.put(ToughEnoughItems.MAGMA_SHARD, new Pair<>(400, FuelType.HEATING));
    }

    private static final int INVENTORY_SIZE = 1;

    private final DefaultedList<ItemStack> inventory;
    private final PropertyDelegate properties;

    private int remainingBurnTime;
    private int itemTotalBurnTime;

    private ClimatizerBlock.Action action = ClimatizerBlock.Action.OFF;

    public ClimatizerBlockEntity() {
        super(ToughEnoughBlockEntities.CLIMATIZER_ENTITY_TYPE);
        inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
        properties = new Properties();
    }

    private enum FuelType {HEATING, COOLING}

    private class Properties implements PropertyDelegate {
        private static final int INDEX_REMAINING_BURN_TIME = 0;
        private static final int INDEX_TOTAL_BURN_TIME = 1;
        private static final int INDEX_ACTION = 2;

        @Override
        public int get(int index) {
            switch (index) {
                case INDEX_REMAINING_BURN_TIME:
                    return remainingBurnTime;
                case INDEX_TOTAL_BURN_TIME:
                    return itemTotalBurnTime;
                case INDEX_ACTION:
                    return getCachedState().get(ClimatizerBlock.ACTION).ordinal();
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case INDEX_REMAINING_BURN_TIME:
                    remainingBurnTime = value;
                case INDEX_TOTAL_BURN_TIME:
                    itemTotalBurnTime = value;
                case INDEX_ACTION:
                    action = ClimatizerBlock.Action.values()[action.ordinal()];
            }
        }

        @Override
        public int size() {
            return 3;
        }
    }

    @Override
    public void tick() {
        if (world != null && !world.isClient()) {
            ItemStack stack = inventory.get(0);

            // Case 1: We still have an item burning
            if (remainingBurnTime > 0) {
                remainingBurnTime--;
                return;
            }

            // Case 2: Our burn time ran out
            // Case 2.1 Time ran out and we have a valid item in the slot
            if (FUEL_ITEMS.containsKey(stack.getItem())) {
                Pair<Integer, FuelType> pair = FUEL_ITEMS.get(stack.getItem());
                remainingBurnTime = pair.getLeft();
                itemTotalBurnTime = pair.getLeft();
                stack.decrement(1);

                switch (pair.getRight()) {
                    case HEATING:
                        world.setBlockState(pos, getCachedState().with(ClimatizerBlock.ACTION, ClimatizerBlock.Action.HEAT));
                        break;
                    case COOLING:
                        world.setBlockState(pos, getCachedState().with(ClimatizerBlock.ACTION, ClimatizerBlock.Action.COOL));
                        break;
                }
                return;
            }

            // Case 1.2 We have no valid item in the slot and the climatizer stops working
            world.setBlockState(pos, getCachedState().with(ClimatizerBlock.ACTION, ClimatizerBlock.Action.OFF));
        }

    }

    @Override
    public void fromTag(BlockState state, CompoundTag compound) {
        super.fromTag(state, compound);
        remainingBurnTime = compound.getInt("remaining");
        itemTotalBurnTime = compound.getInt("total");
        action = ClimatizerBlock.Action.values()[compound.getInt("mode")];
        Inventories.toTag(compound, inventory);
    }

    @Override
    public CompoundTag toTag(CompoundTag compound) {
        compound.putInt("remaining", remainingBurnTime);
        compound.putInt("total", itemTotalBurnTime);
        compound.putInt("mode", action.ordinal());
        Inventories.fromTag(compound, inventory);
        return super.toTag(compound);
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    protected ScreenHandler createScreenHandler(int id, PlayerInventory inventory) {
        return new ClimatizerScreenHandler(id, inventory, this, properties);
    }

    @Override
    public DefaultedList<ItemStack> stacks() {
        return inventory;
    }
}
