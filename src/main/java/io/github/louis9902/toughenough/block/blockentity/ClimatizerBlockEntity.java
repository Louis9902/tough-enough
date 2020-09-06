package io.github.louis9902.toughenough.block.blockentity;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.github.louis9902.toughenough.block.ClimatizerBlock;
import io.github.louis9902.toughenough.block.misc.FuckYouInv;
import io.github.louis9902.toughenough.init.ToughEnoughBlockEntities;
import io.github.louis9902.toughenough.init.ToughEnoughItems;
import io.github.louis9902.toughenough.screenhandler.ClimatizerScreenHandler;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Pair;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

// TODO: consider sidedInventory for this
public class ClimatizerBlockEntity extends LockableContainerBlockEntity implements Tickable, FuckYouInv, NamedScreenHandlerFactory {

    private static final HashMap<Item, Pair<Integer, FuelType>> FUEL_ITEMS = new HashMap<>();
    //private static final ClimatizerFuelRegistry MAP = null;

    static {
        FUEL_ITEMS.put(ToughEnoughItems.ICE_SHARD, new Pair<>(400, FuelType.COOLING));
        FUEL_ITEMS.put(ToughEnoughItems.MAGMA_SHARD, new Pair<>(400, FuelType.HEATING));
    }

    private static final int TICK_COOLDOWN = 20;

    private static final int MAX_EFFECT_STRENGTH = 10;
    private static final int INVENTORY_SIZE = 1;

    // obstruct are blocks which obstruct a block space
    private final Set<BlockPos> obstruct;
    // effect are blocks which are affected by the climatizer
    private final Multimap<Integer, BlockPos> effects;

    private final DefaultedList<ItemStack> inventory;
    private final PropertyDelegate properties;

    private int leftoverBurnTime;
    private int absoluteBurnTime;
    private int cooldown = 0;

    private ClimatizerBlock.Action action = ClimatizerBlock.Action.OFF;

    public ClimatizerBlockEntity() {
        super(ToughEnoughBlockEntities.CLIMATIZER_ENTITY_TYPE);
        inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
        properties = new Properties();

        obstruct = new HashSet<>();
        effects = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
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

    @Override
    public void tick() {
        if (world == null || world.isClient) return;

        ItemStack stack = inventory.get(0);

        // Case 1: There is still an item burning, decrement time
        // Case 2: The burning item time ran out, consume new if possible

        if (leftoverBurnTime > 0) {
            leftoverBurnTime--;
        } else {
            BlockState state = getCachedState();
            Pair<Integer, FuelType> fuel = FUEL_ITEMS.get(stack.getItem());

            if (fuel != null) {
                absoluteBurnTime = leftoverBurnTime = fuel.getLeft();
                stack.decrement(1);

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

        if (cooldown < TICK_COOLDOWN) {
            cooldown++;
            return;
        }

        cooldown = 0;

        // if the spread sets are not up to date, update them and re calc
        if (isSpreadingInvalid() || (effects.isEmpty() && obstruct.isEmpty())) {
            refreshSpreading();
            spawnDebugEntities();
        }

        PlayerStream.around(world, pos, MAX_EFFECT_STRENGTH)
                .filter(p -> effects.values().stream().anyMatch(pos -> p.getBoundingBox().intersects(new Box(pos))))
                .forEach(p -> {
                    p.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.0f);
                    // TODO: apply temporary effect modifier
                });

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

    private void refreshSpreading() {
        effects.clear();
        obstruct.clear();
        spread(Collections.singleton(pos), MAX_EFFECT_STRENGTH);
    }

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

    private List<ArmorStandEntity> entities = new ArrayList<>();

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
