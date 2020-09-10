package io.github.minnecraeft.toughenough.block.entity;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.github.minnecraeft.toughenough.ToughEnough;
import io.github.minnecraeft.toughenough.ToughEnoughComponents;
import io.github.minnecraeft.toughenough.block.ClimatizerBlock;
import io.github.minnecraeft.toughenough.init.ToughEnoughBlockEntities;
import io.github.minnecraeft.toughenough.init.ToughEnoughItems;
import io.github.minnecraeft.toughenough.init.ToughEnoughTags;
import io.github.minnecraeft.toughenough.inventory.InventoryDelegate;
import io.github.minnecraeft.toughenough.screen.ClimatizerScreenHandler;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ClimatizerBlockEntity extends LockableContainerBlockEntity implements Tickable, InventoryDelegate, NamedScreenHandlerFactory {

    // The time in ticks how often the spreading logic is done, we dont do this every tick to save performance
    private static final int SPREADING_COOLDOWN = 20;

    private static final HashMap<Item, Pair<Integer, FuelType>> FUEL_ITEMS = new HashMap<>();

    static {
        FUEL_ITEMS.put(ToughEnoughItems.ICE_SHARD, new Pair<>(400, FuelType.COOLING));
        FUEL_ITEMS.put(ToughEnoughItems.MAGMA_SHARD, new Pair<>(400, FuelType.HEATING));
    }

    // The maximum range the heat can spread from the source block outwards
    private static final int SPREADING_RANGE = 10;
    private final List<ArmorStandEntity> entities = new ArrayList<>();

    private static final int INVENTORY_SIZE = 1;
    // obstruct are blocks which obstruct a block space
    private final Set<BlockPos> obstruct;
    // effect are blocks which are affected by the climatizer

    private final Multimap<Integer, BlockPos> effects;
    private final DefaultedList<ItemStack> inventory;
    // This is used to sync the burning progress to the client to render the progress bar

    private final PropertyDelegate properties;
    // The burn time left for the currently used item
    private int leftoverBurnTime;
    // The total burn time the item currently burning had at the start, this is used to calculate the progress percentage
    private int absoluteBurnTime;
    // The counter since the last spreading update
    private int spreadingCooldownCounter = 0;

    private ClimatizerBlock.Action action = ClimatizerBlock.Action.OFF;

    public ClimatizerBlockEntity() {
        super(ToughEnoughBlockEntities.CLIMATIZER_ENTITY_TYPE);
        inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
        properties = new Properties();

        obstruct = new HashSet<>();
        effects = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return stack.getItem().isIn(ToughEnoughTags.CLIMATIZER_ITEMS);
    }

    @Override
    public void tick() {
        // logic is only done on the server
        if (world == null || world.isClient) return;

        ItemStack fuelItemStack = inventory.get(0);

        // Case 1: There is still an item burning, decrement time
        // Case 2: The burning item time ran out, consume new if possible
        if (leftoverBurnTime > 0) {
            leftoverBurnTime--;
        } else {
            // We modify this state according to our fuel item and set it to the block at the end
            BlockState state = getCachedState();
            Pair<Integer, FuelType> fuel = FUEL_ITEMS.get(fuelItemStack.getItem());

            // Case 2.1: The Item in the fuel Slot is a valid fuel item and we will consume it
            // Case 2.2: The Item is not a valid fuel item and the machine turns off
            if (fuel != null) {
                absoluteBurnTime = leftoverBurnTime = fuel.getLeft();
                fuelItemStack.decrement(1);

                switch (fuel.getRight()) {
                    case HEATING:
                        state = state.with(ClimatizerBlock.ACTION, ClimatizerBlock.Action.HEAT);
                        break;
                    case COOLING:
                        state = state.with(ClimatizerBlock.ACTION, ClimatizerBlock.Action.COOL);
                        break;
                }

            } else {
                state = state.with(ClimatizerBlock.ACTION, ClimatizerBlock.Action.OFF);
            }

            world.setBlockState(pos, state);
        }

        if (spreadingCooldownCounter < SPREADING_COOLDOWN) {
            spreadingCooldownCounter++;
        } else {
            ClimatizerBlock.Action currentAction = getCachedState().get(ClimatizerBlock.ACTION);
            // Only run spreading logic when the machine is turned on,
            if (!currentAction.equals(ClimatizerBlock.Action.OFF)) {
                // if the spread sets are not up to date, update them and re calc
                if (isSpreadingInvalid() || (effects.isEmpty() && obstruct.isEmpty())) {
                    refreshSpreading();
                    spawnDebugEntities();
                }

                // Get any player which is inside the affected area and apply the appropriate temporary effect to them
                PlayerStream.around(world, pos, SPREADING_RANGE)
                        .filter(p -> effects.values().stream().anyMatch(pos -> p.getBoundingBox().intersects(new Box(pos))))
                        .forEach(p -> {
                            switch (currentAction) {
                                case HEAT:
                                    ToughEnoughComponents.TEMPERATURE_MANAGER.get(p).addModifierTarget(ToughEnough.identifier("climatization"), 4, 1);
                                    break;
                                case COOL:
                                    ToughEnoughComponents.TEMPERATURE_MANAGER.get(p).addModifierTarget(ToughEnough.identifier("climatization"), -4, 1);
                                    break;
                            }
                        });
            }
            spreadingCooldownCounter = 0;
        }

    }

    private void refreshSpreading() {
        effects.clear();
        obstruct.clear();
        spread(Collections.singleton(pos), SPREADING_RANGE);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag compound) {
        super.fromTag(state, compound);
        leftoverBurnTime = compound.getInt("remaining");
        absoluteBurnTime = compound.getInt("total");
        action = ClimatizerBlock.Action.values()[compound.getInt("mode")];
        Inventories.toTag(compound, inventory);
    }

    @Override
    public CompoundTag toTag(CompoundTag compound) {
        compound.putInt("remaining", leftoverBurnTime);
        compound.putInt("total", absoluteBurnTime);
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

    // Maps the fuel Item to a pair of 1) the respective burn time 2) whether the item should cool or heat
    private enum FuelType {HEATING, COOLING}

    private void spread(Collection<BlockPos> blocks, int strength) {
        if (strength < 0) return;

        // search for successor if not present it is empty
        Collection<BlockPos> successor = effects.get(strength + 1);
        boolean ignore = successor.isEmpty();

        for (BlockPos pos : blocks) {
            for (Direction direction : Direction.values()) {
                BlockPos offset = pos.offset(direction);
                // if successor is not present or the offset pos is not present in the successor or obstruct
                if (ignore || (!effects.containsValue(offset) && !obstruct.contains(offset))) {
                    boolean b = hasCriteria(offset);
                    if (b) effects.get(strength).add(offset);
                    else obstruct.add(offset);
                }
            }
        }

        spread(effects.get(strength), strength - 1);
    }

    /**
     * Checks if the current block is in a valid state and none of the blocks around has
     * been changed by the player.
     *
     * @return true if the blocks in the collections are up to date
     */
    private boolean isSpreadingInvalid() {
        return obstruct.stream().anyMatch(this::hasCriteria) || effects.values().stream().anyMatch(pos -> !hasCriteria(pos));
    }

    private boolean hasCriteria(BlockPos pos) {
        if (world == null) return false;
        boolean c = world.isSkyVisible(pos);
        if (world == null || c) return false;
        boolean b = world.getBlockState(pos).isFullCube(world, pos);
        return !b;
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        entities.forEach(ArmorStandEntity::kill);
    }

    // Our block specific implementation of the property delegate, this is in a separate class for formatting reasons
    private class Properties implements PropertyDelegate {
        private static final int INDEX_REMAINING_BURN_TIME = 0;
        private static final int INDEX_TOTAL_BURN_TIME = 1;
        private static final int INDEX_ACTION = 2;

        @Override
        public int get(int index) {
            switch (index) {
                case INDEX_REMAINING_BURN_TIME:
                    return leftoverBurnTime;
                case INDEX_TOTAL_BURN_TIME:
                    return absoluteBurnTime;
                case INDEX_ACTION:
                    return getCachedState().get(ClimatizerBlock.ACTION).ordinal();
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case INDEX_REMAINING_BURN_TIME:
                    leftoverBurnTime = value;
                case INDEX_TOTAL_BURN_TIME:
                    absoluteBurnTime = value;
                case INDEX_ACTION:
                    action = ClimatizerBlock.Action.values()[action.ordinal()];
            }
        }

        @Override
        public int size() {
            return 3;
        }
    }

    private void spawnDebugEntities() {
        entities.forEach(ArmorStandEntity::kill);
        entities.clear();
        for (Map.Entry<Integer, BlockPos> e : effects.entries()) {
            BlockPos pos = e.getValue();
            ArmorStandEntity armor = new ArmorStandEntity(world, pos.getX() + .5, pos.getY(), pos.getZ() + .5);
            try {
                Method setMarker = ArmorStandEntity.class.getDeclaredMethod("setMarker", boolean.class);
                setMarker.setAccessible(true);
                setMarker.invoke(armor, true);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
            armor.setInvisible(true);
            armor.setCustomNameVisible(true);
            armor.setCustomName(Text.of(e.getKey().toString()));
            armor.setNoGravity(true);
            entities.add(armor);
            world.spawnEntity(armor);
        }
        for (BlockPos pos : obstruct) {
            ArmorStandEntity armor = new ArmorStandEntity(world, pos.getX() + .5, pos.getY(), pos.getZ() + .5);
            try {
                Method setMarker = ArmorStandEntity.class.getDeclaredMethod("setMarker", boolean.class);
                setMarker.setAccessible(true);
                setMarker.invoke(armor, true);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
            armor.setInvisible(true);
            armor.setCustomNameVisible(true);
            armor.setCustomName(Text.of("<>"));
            armor.setNoGravity(true);
            entities.add(armor);
            world.spawnEntity(armor);
        }
    }

}
