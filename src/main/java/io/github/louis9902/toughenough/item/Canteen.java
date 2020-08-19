package io.github.louis9902.toughenough.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
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

public class Canteen extends Item {
    public Canteen(Settings settings) {
        super(settings);
    }


    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        System.out.println("finished using was called");
        if (!world.isClient) {
            ServerPlayerEntity player = (ServerPlayerEntity) user;
            Criteria.CONSUME_ITEM.trigger(player, stack);
            player.incrementStat(Stats.USED.getOrCreateStat(this));

        }
        if (user instanceof PlayerEntity) {
            System.out.println("used!");
            stack.damage(1, user, (e) -> System.out.println("Item is consumed"));
        }

        return stack;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        //calculate whether the played used the item on a fluid source block
        HitResult hitResult = rayTrace(world, user, RayTraceContext.FluidHandling.SOURCE_ONLY);

        //hit block -> check if it was water and player can modify position
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();

            //player cannot modify block -> we can only drink
            if (!world.canPlayerModifyAt(user, blockPos))
                return ItemUsage.consumeHeldItem(world, user, hand);

            //Player has hit water block -> fill canteen & play sound
            if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
                world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                return fillCanteen(world, user, hand);
            }
        }
        //did not hit a block or it was not water, definitely drink
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    public TypedActionResult<ItemStack> drinkFromCanteen(World world, PlayerEntity user, Hand hand) {
        System.out.println("drinking!");
        ItemStack itemStack = user.getStackInHand(hand);
        itemStack.damage(1, user, (e) -> System.out.println("Item is consumed"));
        return TypedActionResult.consume(itemStack);
    }

    public TypedActionResult<ItemStack> fillCanteen(World world, PlayerEntity user, Hand hand) {
        System.out.println("fillinga!");
        ItemStack itemStack = user.getStackInHand(hand);
        itemStack.setDamage(0);
        return TypedActionResult.success(itemStack);
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


}
