package io.github.minnecraeft.toughenough.item;

import io.github.minnecraeft.toughenough.api.thirst.Drink;
import io.github.minnecraeft.toughenough.thirst.drinks.DefaultDrink;
import io.github.minnecraeft.toughenough.thirst.drinks.WaterType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.sound.SoundCategory.NEUTRAL;
import static net.minecraft.sound.SoundEvents.ITEM_BOTTLE_FILL;

public class CanteenItem extends DrinkItem {

    private static final int MAX_DAMAGE = 4;

    public CanteenItem(Settings settings) {
        super(settings.maxDamage(MAX_DAMAGE));
    }

    @Override
    public @NotNull Drink componentToAttach(ItemStack stack) {
        return new DefaultDrink(true, WaterType.NORMAL);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        // calculate whether the played used the item on a fluid source block
        BlockHitResult result = raycast(world, player, RaycastContext.FluidHandling.SOURCE_ONLY);

        if (result.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();

            // player cannot modify block -> we can only drink
            if (!world.canPlayerModifyAt(player, pos)) {
                return super.use(world, player, hand);
            }

            // player has hit water block -> fill canteen
            if (world.getFluidState(pos).isIn(FluidTags.WATER)) {
                return fillCanteen(world, player, hand, pos);
            }

            BlockState state = world.getBlockState(pos);

            if (state.getBlock().is(Blocks.CAULDRON)) {
                ItemStack stack = player.getStackInHand(hand);
                int level = state.get(CauldronBlock.LEVEL);

                if (stack.getDamage() > 0 && level > 0 && !world.isClient) {
                    if (!player.isCreative()) {
                        player.incrementStat(Stats.USE_CAULDRON);
                        ((CauldronBlock) state.getBlock()).setLevel(world, pos, state, level - 1);
                    }
                    return fillCanteen(world, player, hand, pos);
                }
            }
        }
        // did not hit a block or it was not water -> definitely drink
        return super.use(world, player, hand);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        stack.setDamage(3);
    }

    @Override
    protected boolean canConsume(PlayerEntity player, ItemStack stack) {
        return super.canConsume(player, stack) && hasFilling(stack);
    }

    @Override
    protected ItemStack onConsume(PlayerEntity player, ItemStack stack) {
        int damage = stack.getDamage() + 1;

        if (damage > 0 && damage < MAX_DAMAGE) {
            stack.setDamage(damage);
            // canteen is empty hide damage bar
            if (damage == 3) {
                stack.getOrCreateTag().putBoolean("Unbreakable", true);
            } else if (stack.getTag() != null && stack.getTag().contains("Unbreakable")) {
                stack.getTag().remove("Unbreakable");
            }
        }
        return stack;
    }

    public TypedActionResult<ItemStack> fillCanteen(World world, PlayerEntity player, Hand hand, BlockPos pos) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getDamage() > 0) {
            stack.setDamage(0);
            world.playSound(player, pos, ITEM_BOTTLE_FILL, NEUTRAL, 1.0F, 1.0F);
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(stack);
    }

    public static boolean hasFilling(ItemStack stack) {
        // maximum damage is reserved for "broken" model state
        return stack.getDamage() < stack.getMaxDamage() - 1;
    }

}
