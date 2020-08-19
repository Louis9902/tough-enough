package io.github.louis9902.toughenough.StatusEffect;

import io.github.louis9902.toughenough.stats.ThirstManager;
import io.github.louis9902.toughenough.stats.Thirsty;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;

public class Thirst extends StatusEffect {
    public Thirst(StatusEffectType type, int color) {
        super(type, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            ThirstManager thirst = ((Thirsty) player).getThirstManager();
            thirst.setExhaustion(thirst.getExhaustion() + getExhaustionModifier(amplifier));
            System.out.println("Thirst.applyUpdateEffect");
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    private float getExhaustionModifier(int amplifier) {
        return 0.005F * (amplifier + 1);
    }
}
