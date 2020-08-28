package io.github.louis9902.toughenough.item;

import io.github.louis9902.toughenough.api.thirst.Drink;
import io.github.louis9902.toughenough.components.DefaultDrink;
import io.github.louis9902.toughenough.item.drink.JuiceType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.jetbrains.annotations.NotNull;

public class JuiceItem extends DrinkItem {

    private final Drink drink;
    private final int color;

    public JuiceItem(Settings settings, JuiceType juice) {
        this(settings, juice, juice.color);
    }

    public JuiceItem(Settings settings, Drink.Modifiers modifiers, int color) {
        super(settings);
        this.drink = new DefaultDrink(false, modifiers);
        this.color = color;
    }

    public static int colorForStack(ItemStack stack, int index) {
        return index > 0 ? -1 : ((JuiceItem) stack.getItem()).color;
    }

    @Override
    public @NotNull Drink componentToAttach(ItemStack stack) {
        return drink;
    }

    @Override
    protected ItemStack onConsume(PlayerEntity player, ItemStack stack) {
        if (player.isCreative()) return stack;
        ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
        stack.decrement(1);
        if (stack.isEmpty()) {
            return bottle;
        }
        if (player.inventory.insertStack(bottle)) {
            return stack;
        }
        player.dropStack(bottle);
        return stack;
    }
}
