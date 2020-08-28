package io.github.louis9902.toughenough.entity.effect;

import io.github.louis9902.toughenough.ToughEnoughComponents;
import io.github.louis9902.toughenough.api.thirst.ThirstManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class ThirstStatusEffect extends StatusEffect {

    public ThirstStatusEffect() {
        super(StatusEffectType.HARMFUL, 97 << 16 | 213 << 8 | 26);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        ThirstManager manager = ToughEnoughComponents.THIRST_MANAGER.getNullable(entity);
        assert manager != null;
        manager.addExhaustion(calcExhaustionModifier(amplifier));
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    private float calcExhaustionModifier(int amplifier) {
        return 0.005F * (amplifier + 1);
    }
}
