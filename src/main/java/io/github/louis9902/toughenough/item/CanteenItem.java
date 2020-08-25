package io.github.louis9902.toughenough.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

import static net.minecraft.sound.SoundCategory.NEUTRAL;
import static net.minecraft.sound.SoundEvents.ITEM_BOTTLE_FILL;

public class CanteenItem extends DrinkItem implements DamageBypassItem {

    public CanteenItem(Settings settings, int thirst, float hydration) {
        super(settings.maxDamage(4), thirst, hydration);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        // calculate whether the played used the item on a fluid source block
        BlockHitResult result = rayTrace(world, player, RayTraceContext.FluidHandling.SOURCE_ONLY);

        if (result.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();

            // player cannot modify block -> we can only drink
            if (!world.canPlayerModifyAt(player, pos))
                return super.use(world, player, hand);

            // player has hit water block -> fill canteen
            if (world.getFluidState(pos).isIn(FluidTags.WATER))
                return fillCanteen(world, player, hand, pos);
        }
        // did not hit a block or it was not water -> definitely drink
        return super.use(world, player, hand);
    }

    @Override
    protected boolean canConsume(PlayerEntity player, ItemStack stack) {
        return super.canConsume(player, stack) && hasFilling(stack);
    }

    @Override
    protected ItemStack consume(PlayerEntity player, ItemStack stack) {
        // canteen shouldn't break at any point as we have a broken state
        stack.damage(1, player, (e) -> {
            throw new IllegalStateException("canteen shouldn't be broken");
        });
        return stack;
    }

    public TypedActionResult<ItemStack> fillCanteen(World world, PlayerEntity player, Hand hand, BlockPos pos) {
        System.out.println("CanteenItem.fillCanteen");
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

    @Override
    public boolean isDamaged(ItemStack stack) {
        return stack.isDamageable() && stack.getDamage() < getMaxDamage() - 1;
    }

    @Override
    public int getDamage(ItemStack stack) {
        return stack.getDamage();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getMaxDamage() - 1;
    }
}
