package io.github.louis9902.toughenough.entity.damage;

import net.minecraft.entity.damage.DamageSource;

public class DamageSources extends DamageSource {

    public static final DamageSource THIRST = new DamageSources("thirst").setBypassesArmor().setBypassesArmor();

    protected DamageSources(String name) {
        super(name);
    }

    @Override
    protected DamageSources setUnblockable() {
        super.setUnblockable();
        return this;
    }

    @Override
    protected DamageSources setBypassesArmor() {
        super.setBypassesArmor();
        return this;
    }
}
