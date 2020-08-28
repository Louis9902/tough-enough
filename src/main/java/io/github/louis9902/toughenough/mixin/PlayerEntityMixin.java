package io.github.louis9902.toughenough.mixin;

import io.github.louis9902.toughenough.api.temperature.TemperatureManager;
import io.github.louis9902.toughenough.api.thirst.ThirstManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.louis9902.toughenough.ToughEnoughComponents.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {

    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /**
     * Our ThirstManager needs to be updated every tick, therefore we mixin into the tick method and get the
     * {@link ThirstManager ThirstManager Component} and call its update method.
     * <p>
     * If the component is not present for some reason, nothing will be done
     */
    @Inject(at = @At(value = "HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        if (!world.isClient) {
            THIRST_MANAGER.maybeGet(this).ifPresent(ThirstManager::update);
            TEMPERATURE_MANAGER.maybeGet(this).ifPresent(TemperatureManager::update);
        }
    }

    /**
     * Every time vanilla code adds exhaustion to the hunger system we add the same amount to our thirst manager.
     * This means both exhaustion values grow at the same rate, in our thirst manager we can tweak the threshold for
     * subtracting a thirst level however.
     * <p>
     * If the component is not present for some reason, nothing will be done
     */
    @Redirect(method = "addExhaustion", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V"
    ))
    public void addExhaustion(HungerManager manager, float exhaustion) {
        manager.addExhaustion(exhaustion);
        THIRST_MANAGER.maybeGet(this).ifPresent((val) -> val.addExhaustion(exhaustion));
    }

    /**
     * Each time the player eats food, we also need to check weather the eaten food has a thirst component as well,
     * if yes we need to drink it.
     */
    @Inject(method = "eatFood", at = @At(
            value = "HEAD"
    ))
    private void eatFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> ci) {
        DRINKABLE.maybeGet(stack).ifPresent(drink -> THIRST_MANAGER.maybeGet(this).ifPresent(manager -> manager.drink(drink)));
    }
}
