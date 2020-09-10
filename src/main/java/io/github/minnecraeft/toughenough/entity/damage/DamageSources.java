package io.github.minnecraeft.toughenough.entity.damage;

import net.minecraft.entity.damage.DamageSource;

public class DamageSources extends DamageSource {

    public static final DamageSource THIRST = new DamageSources("thirst").setBypassesArmor().setBypassesArmor();

    protected DamageSources(String name) {
        super(name);
    }

    @Override
    public DamageSources setProjectile() {
        super.setProjectile();
        return this;
    }

    @Override
    public DamageSources setExplosive() {
        super.setExplosive();
        return this;
    }

    @Override
    protected DamageSources setOutOfWorld() {
        super.setOutOfWorld();
        return this;
    }

    @Override
    protected DamageSources setFire() {
        super.setFire();
        return this;
    }

    @Override
    public DamageSources setScaledWithDifficulty() {
        super.setScaledWithDifficulty();
        return this;
    }

    @Override
    public DamageSources setUsesMagic() {
        super.setUsesMagic();
        return this;
    }

    @Override
    public DamageSources setUnblockable() {
        super.setUnblockable();
        return this;
    }

    @Override
    public DamageSources setBypassesArmor() {
        super.setBypassesArmor();
        return this;
    }
}
