package io.github.louis9902.toughenough.mixin;

import io.github.louis9902.toughenough.stats.ThirstManager;
import io.github.louis9902.toughenough.stats.Thirsty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity implements Thirsty {

    protected ThirstManager thirst = new ThirstManager();

    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;incrementStat(Lnet/minecraft/util/Identifier;)V"
    ))
    public void tickInject(CallbackInfo ci) {
        thirst.update((PlayerEntity) (Object) this);
    }

    @Inject(method = "writeCustomDataToTag", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerAbilities;serialize(Lnet/minecraft/nbt/CompoundTag;)V"
    ))
    public void writeCustomDataToTag(CompoundTag compound, CallbackInfo ci) {
        thirst.toTag(compound);
    }

    @Inject(method = "readCustomDataFromTag", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerAbilities;deserialize(Lnet/minecraft/nbt/CompoundTag;)V"
    ))
    public void readCustomDataFromTag(CompoundTag compound, CallbackInfo ci) {
        thirst.fromTag(compound);
    }

    @Override
    public ThirstManager getThirstManager() {
        return thirst;
    }
}
