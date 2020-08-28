package io.github.louis9902.toughenough.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class HypothermiaStatusEffect extends StatusEffect {

    public HypothermiaStatusEffect() {
        super(StatusEffectType.HARMFUL, 168 << 16 | 207 << 8 | 255);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
