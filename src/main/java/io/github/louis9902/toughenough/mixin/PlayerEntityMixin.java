package io.github.louis9902.toughenough.mixin;

import io.github.louis9902.toughenough.MyComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {

    @Shadow
    public abstract void addExhaustion(float exhaustion);

    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /**
     * Our ThirstManager needs to be updated every tick, therefore we mixin into the tick method and get the thirstmanager
     * component and call its update method. If the component is not present for some reason, nothing will be done
     *
     * @param ci
     */
    @Inject(method = "tick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;incrementStat(Lnet/minecraft/util/Identifier;)V"
    ))
    public void tickInject(CallbackInfo ci) {
        MyComponents.THIRSTY.maybeGet(this).ifPresent((val) -> val.update((PlayerEntity) (Object) this));
    }

    /**
     * Every time vanilla code adds exhaustion to the hunger system we add the same amount to our thirst manager,
     * This means both exhaustion values grow at the same rate, in our thirst manager we can tweak the threshold for
     * subtracting a thirst level however. If the component is not present for some reason, nothing will be done
     *
     * @param manager
     * @param exhaustion
     */
    @Redirect(method = "addExhaustion", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V"
    ))
    public void Exhaustion(HungerManager manager, float exhaustion) {
        manager.addExhaustion(exhaustion);
        MyComponents.THIRSTY.maybeGet(this).ifPresent((val) -> val.addExhaustion(exhaustion));
    }
}
