package io.github.louis9902.toughenough.StatusEffect;

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
        if(entity instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity) entity;
        }
    }
}
