package io.github.louis9902.toughenough.block.blockentity;

import io.github.louis9902.toughenough.block.Climatizer;
import io.github.louis9902.toughenough.block.misc.FuckYouInv;
import io.github.louis9902.toughenough.init.ToughEnoughBlockEntities;
import io.github.louis9902.toughenough.init.ToughEnoughItems;
import io.github.louis9902.toughenough.screenhandler.ClimatizerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
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

//Todo consider sidedInventory for this
public class ClimatizerEntity extends LockableContainerBlockEntity implements Tickable, FuckYouInv, NamedScreenHandlerFactory {
    private static final HashMap<Item, Pair<Integer, FuelType>> FUEL_ITEMS = new HashMap<>();

    private static final int INVENTORY_SIZE = 1;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);

    static {
        FUEL_ITEMS.put(ToughEnoughItems.ICE_SHARD, new Pair<>(400, FuelType.COOLING));
        FUEL_ITEMS.put(ToughEnoughItems.MAGMA_SHARD, new Pair<>(400, FuelType.HEATING));
    }

    private int remainingBurnTime;
    private int itemTotalBurnTime;
    private Climatizer.Action action = Climatizer.Action.OFF;
    PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return remainingBurnTime;
                case 1:
                    return itemTotalBurnTime;
                case 2:
                    return getCachedState().get(Climatizer.ACTION).ordinal();
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    remainingBurnTime = value;
                case 1:
                    itemTotalBurnTime = value;
                case 2:
                    action = Climatizer.Action.values()[action.ordinal()];
            }
        }

        @Override
        public int size() {
            return 3;
        }
    };

    @Override
    public void tick() {

        if (!world.isClient()) {
            ItemStack fuelStack = inventory.get(0);

            //Case 1: We still have an item burning
            if (remainingBurnTime > 0) {
                remainingBurnTime--;
            }
            //Case 2: Our burn time ran out
            else if (remainingBurnTime == 0) {
                //Case 2.1 Time ran out and we have a valid item in the slot
                if (FUEL_ITEMS.containsKey(fuelStack.getItem())) {
                    Pair<Integer, FuelType> pair = FUEL_ITEMS.get(fuelStack.getItem());
                    remainingBurnTime = pair.getLeft();
                    itemTotalBurnTime = pair.getLeft();
                    fuelStack.decrement(1);

                    switch (pair.getRight()) {
                        case HEATING:
                            world.setBlockState(pos, getCachedState().with(Climatizer.ACTION, Climatizer.Action.HEAT));
                            break;
                        case COOLING:
                            world.setBlockState(pos, getCachedState().with(Climatizer.ACTION, Climatizer.Action.COOL));
                            break;
                    }
                }
                //Case 1.2 We have no valid item in the slot and the climatizer stops working
                else {
                    world.setBlockState(pos, getCachedState().with(Climatizer.ACTION, Climatizer.Action.OFF));
                }

            }
        }

    }

    public ClimatizerEntity() {
        super(ToughEnoughBlockEntities.CLIMATIZER_ENTITY_TYPE);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        remainingBurnTime = tag.getInt("remaining");
        itemTotalBurnTime = tag.getInt("total");
        action = Climatizer.Action.values()[tag.getInt("mode")];
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("remaining", remainingBurnTime);
        tag.putInt("total", itemTotalBurnTime);
        tag.putInt("mode", action.ordinal());
        return super.toTag(tag);
    }

    private enum FuelType {
        HEATING, COOLING
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
