package io.github.louis9902.toughenough.block.blockentity;

import io.github.louis9902.toughenough.block.ClimatizerBlock;
import io.github.louis9902.toughenough.block.misc.FuckYouInv;
import io.github.louis9902.toughenough.init.ToughEnoughBlockEntities;
import io.github.louis9902.toughenough.init.ToughEnoughItems;
import io.github.louis9902.toughenough.screenhandler.ClimatizerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: consider sidedInventory for this
public class ClimatizerBlockEntity extends LockableContainerBlockEntity implements Tickable, FuckYouInv, NamedScreenHandlerFactory {

    private static final HashMap<Item, Pair<Integer, FuelType>> FUEL_ITEMS = new HashMap<>();
    private static final int MAX_SPREAD = 10;
    private final Set<BlockPos> obstructed;

    static {
        FUEL_ITEMS.put(ToughEnoughItems.ICE_SHARD, new Pair<>(400, FuelType.COOLING));
        FUEL_ITEMS.put(ToughEnoughItems.MAGMA_SHARD, new Pair<>(400, FuelType.HEATING));
    }

    private static final int INVENTORY_SIZE = 1;
    private final Set<BlockPos>[] filled;

    private final DefaultedList<ItemStack> inventory;
    private final PropertyDelegate properties;

    private int remainingBurnTime;
    private int itemTotalBurnTime;
    private boolean firstUpdate = true;

    private ClimatizerBlock.Action action = ClimatizerBlock.Action.OFF;

    public ClimatizerBlockEntity() {
        super(ToughEnoughBlockEntities.CLIMATIZER_ENTITY_TYPE);
        inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
        properties = new Properties();

        obstructed = new HashSet<>();
        filled = new Set[MAX_SPREAD + 1];
        for (int i = 0; i < MAX_SPREAD + 1; i++) {
            filled[i] = new HashSet<>();
        }
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

            if (firstUpdate || !isSpreadUpToDate()) {
                firstUpdate = false;
                updateSpread();

                //get players inside the maximum range of the block, we still need to check wether they are actually in the
                //affected blocks
                List<PlayerEntity> nonSpectatingEntities = world.getNonSpectatingEntities(PlayerEntity.class, getMaximumBox());

                for (PlayerEntity player : nonSpectatingEntities) {
                    if (isPlayerAffected(player)) {
                        System.out.println("Player Affected!");
                    }
                }

            }
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

    private void updateSpread() {
        resetSpread();

        //We don't use trackAroundPos here as that would result in index being out of bounds
        for (Direction dir : Direction.values()) {
            if (isValidPosition(pos.offset(dir)))
                filled[MAX_SPREAD].add(pos.offset(dir));
        }

        runSpreading(MAX_SPREAD - 1);

        //Debugging: check that there are no duplicates between the different sets
        /*Stream<BlockPos> blockPosStream = Arrays.stream(filled).flatMap(Collection::stream);
        if (blockPosStream.distinct().count() != blockPosStream.count()) {
            throw new IllegalStateException("There should not be any intersecting elemements in the set of tracked blocks");
        }*/
    }

    //Recursively tracks all blocks around all tracked blocks with the current strength
    private void runSpreading(int strength) {
        //This check is necessary for the recursion to end
        if (strength > 0) {
            for (BlockPos tracked : filled[strength + 1]) {
                trackAroundPos(tracked, strength);
            }
            runSpreading(strength - 1);
        }
    }

    private void trackAroundPos(BlockPos pos, int strength) {
        for (Direction dir : Direction.values()) {
            BlockPos offset = pos.offset(dir);

            //Do not add to set if it is already contained in last step.
            //In other words: Don't go backwards when spreading
            if (!filled[strength + 1].contains(offset)) {
                if (isValidPosition(offset))
                    filled[strength].add(offset);
                else
                    obstructed.add(pos);
            }
        }
    }


    private void resetSpread() {
        obstructed.clear();
        for (Set<BlockPos> set : filled)
            set.clear();
    }

    private boolean isSpreadUpToDate() {
        for (BlockPos pos : obstructed) {
            if (isValidPosition(pos))
                return false;
        }
        for (Set<BlockPos> set : filled) {
            for (BlockPos pos : set) {
                if (!isValidPosition(pos))
                    return false;
            }
        }
        return true;
    }

    private boolean isValidPosition(BlockPos pos) {
        return !world.getBlockState(pos).isFullCube(world, pos) && !world.isSkyVisible(pos);
    }

    private Box getMaximumBox() {
        return new Box(pos).expand(MAX_SPREAD);
    }

    private boolean isPlayerAffected(PlayerEntity playerEntity) {
        //Only check within the distance between the block and the player
        int distance = (int) Math.floor(playerEntity.getBlockPos().getSquaredDistance(pos));

        for (int i = MAX_SPREAD; i >= distance; --i) {
            for (BlockPos pos : filled[i])

                if (playerEntity.getBoundingBox().intersects(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1))
                    return true;
        }
        return false;
    }
}
