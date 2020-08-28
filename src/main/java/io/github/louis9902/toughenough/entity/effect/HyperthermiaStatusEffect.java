package io.github.louis9902.toughenough.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class HyperthermiaStatusEffect extends StatusEffect {

    public HyperthermiaStatusEffect() {
        super(StatusEffectType.HARMFUL, 255 << 16 | 163 << 8 | 2);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

}
