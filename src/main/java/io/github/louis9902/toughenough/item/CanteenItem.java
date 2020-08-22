package io.github.louis9902.toughenough.item;

import io.github.louis9902.toughenough.components.ThirstManager;
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

import java.util.Optional;

import static io.github.louis9902.toughenough.ToughEnoughComponents.THIRSTY;
import static net.minecraft.sound.SoundCategory.NEUTRAL;
import static net.minecraft.sound.SoundEvents.ITEM_BOTTLE_FILL;

public class CanteenItem extends Item {

    public CanteenItem(Settings settings) {
        super(settings.maxDamage(4));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        System.out.println("CanteenItem.use");
        // calculate whether the played used the item on a fluid source block
        BlockHitResult result = rayTrace(world, player, RayTraceContext.FluidHandling.SOURCE_ONLY);

        if (result.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();

            // player cannot modify block -> we can only drink
            if (!world.canPlayerModifyAt(player, pos))
                return tryDrink(world, player, hand);

            // player has hit water block -> fill canteen
            if (world.getFluidState(pos).isIn(FluidTags.WATER)) {
                return fillCanteen(world, player, hand, pos);
            }
        }
        // did not hit a block or it was not water -> definitely drink
        return tryDrink(world, player, hand);
    }

    // make sure the player can only drink if the canteen wouldn't reach its reserved damage value
    private TypedActionResult<ItemStack> tryDrink(World world, PlayerEntity player, Hand hand) {
        System.out.println("CanteenItem.drink");
        ItemStack stack = player.getStackInHand(hand);
        Optional<ThirstManager> manager = THIRSTY.maybeGet(player);

        boolean canDrink = manager.map(mn -> mn.getThirst() < ThirstManager.getMaxThirst()).orElse(false);

        if (hasFilling(stack) && canDrink) {
            return ItemUsage.consumeHeldItem(world, player, hand);
        }

        return TypedActionResult.pass(player.getStackInHand(hand));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        System.out.println("CanteenItem.finishUsing");
        if (!world.isClient) {
            ServerPlayerEntity player = (ServerPlayerEntity) user;
            Criteria.CONSUME_ITEM.trigger(player, stack);
            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        if (user instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) user;

            // canteen shouldn't break at any point as we have a broken state
            stack.damage(1, player, (e) -> {
                throw new IllegalStateException("canteen shouldn't be broken");
            });

            THIRSTY.maybeGet(player).ifPresent(manager -> manager.drink(stack));
        }

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

    public static boolean hasFilling(ItemStack stack) {
        // maximum damage is reserved for "broken" model state
        return stack.getDamage() < stack.getMaxDamage() - 1;
    }

}
