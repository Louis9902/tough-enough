package io.github.louis9902.toughenough.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

import static io.github.louis9902.toughenough.ToughEnoughComponents.THIRSTY;

public class ThirstStatusEffect extends StatusEffect {

    public ThirstStatusEffect(StatusEffectType type, int color) {
        super(type, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        THIRSTY.maybeGet(entity).ifPresent((manager -> manager.addExhaustion(calcExhaustionModifier(amplifier))));
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    private float calcExhaustionModifier(int amplifier) {
        return 0.005F * (amplifier + 1);
    }
}
