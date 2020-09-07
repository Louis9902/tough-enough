package io.github.louis9902.toughenough.api.temperature;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class Climatization {
    private Identifier identifier;
    private int amount;
    private int rate;
    private int endTime;

    public Climatization(CompoundTag compound) {
        decode(compound);
    }

    public Climatization(Identifier identifier, int amount, int rate, int endTime) {
        this.identifier = identifier;
        this.amount = amount;
        this.rate = rate;
        this.endTime = endTime;
    }

    public CompoundTag encode() {
        CompoundTag compound = new CompoundTag();
        compound.putString("Id", identifier.toString());
        compound.putInt("Amount", amount);
        compound.putInt("Rate", rate);
        compound.putInt("EndTime", endTime);
        return compound;
    }

    public void decode(CompoundTag compound) {
        identifier = new Identifier(compound.getString("Id"));
        amount = compound.getInt("Amount");
        rate = compound.getInt("Rate");
        endTime = compound.getInt("EndTime");
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public int getAmount() {
        return amount;
    }

    public int getRate() {
        return rate;
    }

    public int getEndTime() {
        return endTime;
    }
}
