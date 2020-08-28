package io.github.louis9902.toughenough.item;

import io.github.louis9902.toughenough.ToughEnoughComponents;
import io.github.louis9902.toughenough.api.thirst.Drink;
import io.github.louis9902.toughenough.api.thirst.ThirstManager;
import io.github.louis9902.toughenough.init.ToughEnoughStatusEffects;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class DrinkItem extends Item {

    public DrinkItem(Settings settings) {
        super(settings);
    }

    @NotNull
    public abstract Drink componentToAttach(ItemStack stack);

    protected boolean canConsume(PlayerEntity player, ItemStack stack) {
        ThirstManager manager = ToughEnoughComponents.THIRST_MANAGER.get(player);
        return player.abilities.invulnerable || manager.isThirsty();
    }

    protected void applyEffects(PlayerEntity player, Drink drink) {
        if (player.world.random.nextFloat() < drink.getPoisonChance()) {
            player.addStatusEffect(new StatusEffectInstance(ToughEnoughStatusEffects.THIRST, 600));
        }
    }

    protected ItemStack onConsume(PlayerEntity player, ItemStack stack) {
        return stack;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (canConsume(user, stack)) {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(stack);
        }

        return TypedActionResult.fail(stack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient) {
            ServerPlayerEntity player = (ServerPlayerEntity) user;
            Criteria.CONSUME_ITEM.trigger(player, stack);
            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        if (user instanceof PlayerEntity) {
            ThirstManager manager = ToughEnoughComponents.THIRST_MANAGER.get(user);
            Drink drink = ToughEnoughComponents.DRINKABLE.get(stack);

            manager.drink(drink);
            applyEffects(((PlayerEntity) user), drink);

            return onConsume(((PlayerEntity) user), stack);
        }

        return stack;
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
