package io.github.minnecraeft.toughenough.entity.effect;

import io.github.minnecraeft.toughenough.ToughEnoughComponents;
import io.github.minnecraeft.toughenough.api.thirst.ThirstManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;

public class ThirstStatusEffect extends StatusEffect {

    public ThirstStatusEffect() {
        super(StatusEffectType.HARMFUL, 97 << 16 | 213 << 8 | 26);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            ThirstManager manager = ToughEnoughComponents.THIRST_MANAGER.get(entity);
            manager.addExhaustion(calcExhaustionModifier(amplifier));
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    private float calcExhaustionModifier(int amplifier) {
        return 0.005F * (amplifier + 1);
    }
}
