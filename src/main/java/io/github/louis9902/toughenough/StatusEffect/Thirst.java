package io.github.louis9902.toughenough.StatusEffect;

import io.github.louis9902.toughenough.ToughEnough;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class Thirst extends StatusEffect {
    public Thirst(StatusEffectType type, int color) {
        super(type, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        ToughEnough.THIRSTY.maybeGet(entity).ifPresent(
                (thirstyManager -> thirstyManager.setExhaustion(thirstyManager.getExhaustion() + getExhaustionModifier(amplifier))));
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    private float getExhaustionModifier(int amplifier) {
        return 0.005F * (amplifier + 1);
    }
}
