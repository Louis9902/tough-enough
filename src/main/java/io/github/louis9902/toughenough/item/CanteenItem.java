package io.github.louis9902.toughenough.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

import static io.github.louis9902.toughenough.ToughEnoughComponents.THIRSTY;
import static net.minecraft.sound.SoundCategory.NEUTRAL;
import static net.minecraft.sound.SoundEvents.ITEM_BOTTLE_FILL;

public class CanteenItem extends Item {

    public CanteenItem(Settings settings) {
        super(settings.maxDamage(4));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient) {
            ServerPlayerEntity player = (ServerPlayerEntity) user;
            Criteria.CONSUME_ITEM.trigger(player, stack);
            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        if (user instanceof PlayerEntity) {
            return drinkFromCanteen((PlayerEntity) user, user.getActiveHand()).getValue();
        }

        return stack;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // calculate whether the played used the item on a fluid source block
        BlockHitResult result = rayTrace(world, user, RayTraceContext.FluidHandling.SOURCE_ONLY);

        if (result.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();

            // player cannot modify block -> we can only drink
            if (!world.canPlayerModifyAt(user, pos))
                return consumeIfDrinkable(world, user, hand);

            // player has hit water block -> fill canteen & play sound
            if (world.getFluidState(pos).isIn(FluidTags.WATER)) {
                world.playSound(user, user.getX(), user.getY(), user.getZ(), ITEM_BOTTLE_FILL, NEUTRAL, 1.0F, 1.0F);
                return fillCanteen(user, hand);
            }
        }
        // did not hit a block or it was not water -> definitely drink
        return consumeIfDrinkable(world, user, hand);
    }

    public TypedActionResult<ItemStack> drinkFromCanteen(PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (isDrinkable(stack)) {

            // canteen shouldn't break at any point as we have a broken state
            stack.damage(1, user, (e) -> {
                throw new IllegalStateException("canteen shouldn't be broken");
            });

            THIRSTY.maybeGet(user).ifPresent(manager -> manager.drink(stack));

            return TypedActionResult.consume(stack);
        }
        return TypedActionResult.pass(stack);
    }

    public TypedActionResult<ItemStack> fillCanteen(PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        stack.setDamage(0);
        return TypedActionResult.success(stack);
    }

    @Override
    public SoundEvent getDrinkSound() {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 20;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    public static boolean isDrinkable(ItemStack stack) {
        // maximum damage is reserved for "broken" model state
        return stack.getDamage() < stack.getMaxDamage() - 1;
    }

    // make sure the player can only drink if the canteen wouldn't reach its reserved damage value
    private TypedActionResult<ItemStack> consumeIfDrinkable(World world, PlayerEntity user, Hand hand) {
        if (isDrinkable(user.getStackInHand(hand)))
            return ItemUsage.consumeHeldItem(world, user, hand);
        else
            return TypedActionResult.pass(user.getStackInHand(hand));
    }
}
