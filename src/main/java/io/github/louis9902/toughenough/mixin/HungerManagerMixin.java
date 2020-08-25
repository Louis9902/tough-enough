package io.github.louis9902.toughenough.mixin;

import io.github.louis9902.toughenough.components.ThirstManager;
import io.github.louis9902.toughenough.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.louis9902.toughenough.ToughEnoughComponents.THIRSTY;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {

    @Shadow
    private float foodSaturationLevel;

    @Shadow
    private int foodStarvationTimer;

    @Shadow
    public abstract void addExhaustion(float exhaustion);

    @Shadow
    private int foodLevel;

    private boolean canHungerFastHeal(PlayerEntity player) {
        return this.foodSaturationLevel > 0.0F && this.foodLevel >= 20;
    }

    private boolean canThirstFastHeal(PlayerEntity player) {
        ThirstManager manager = THIRSTY.maybeGet(player).orElseThrow(IllegalStateException::new);
        return manager.getHydration() > 0.0F && manager.getThirst() >= 20;
    }

    private boolean canHungerHeal(PlayerEntity player) {
        return this.foodLevel >= 18;
    }

    private boolean canThirstHeal(PlayerEntity player) {
        ThirstManager manager = THIRSTY.maybeGet(player).orElseThrow(IllegalStateException::new);
        return manager.getThirst() >= 18;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getGameRules()Lnet/minecraft/world/GameRules;"),
            method = "update", cancellable = true)
    public void update(PlayerEntity player, CallbackInfo ci) {
        ThirstManager manager = THIRSTY.maybeGet(player).orElseThrow(IllegalStateException::new);

        Difficulty difficulty = player.world.getDifficulty();

        GameRules rules = player.world.getGameRules();
        boolean regeneration = rules.getBoolean(GameRules.NATURAL_REGENERATION);

        boolean canTick = false;

        if (regeneration && player.canFoodHeal()) {
            canTick = true;
            if (canHungerFastHeal(player) && canThirstFastHeal(player)) {
                if (this.foodStarvationTimer >= 10) {
                    float f = Math.min(this.foodSaturationLevel, 6.0F);
                    player.heal(f / 6.0F);
                    this.addExhaustion(f);
                    this.foodStarvationTimer = 0;
                }
            } else if (canHungerHeal(player) && canThirstHeal(player)) {
                if (this.foodStarvationTimer >= 80) {
                    player.heal(1.0F);
                    this.addExhaustion(6.0F);
                    this.foodStarvationTimer = 0;
                }
            }
        }

        if (this.foodLevel <= 0 || manager.getThirst() <= 0) {
            canTick = true;

            DamageSource source = this.foodLevel <= 0 ? DamageSources.STARVE : DamageSources.THIRST;

            if (this.foodStarvationTimer >= 80) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.damage(source, 1.0F);
                }

                this.foodStarvationTimer = 0;
            }
        }

        if (canTick) this.foodStarvationTimer++;


        ci.cancel();
    }

}
