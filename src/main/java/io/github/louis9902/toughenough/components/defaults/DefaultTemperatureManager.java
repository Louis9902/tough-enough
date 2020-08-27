package io.github.louis9902.toughenough.components.defaults;

import io.github.louis9902.toughenough.components.TemperatureManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;

import static io.github.louis9902.toughenough.ToughEnoughComponents.HEATY;

public class DefaultTemperatureManager implements TemperatureManager {
    final Entity provider;
    int temperature = 0;

    public DefaultTemperatureManager(Entity provider) {
        this.provider = provider;
    }

    @Override
    public int getTemperature() {
        return temperature;
    }

    @Override
    public void setTemperature(int t) {
        temperature = t;
        sync();
    }

    @Override
    public void update() {

    }

    //We override this so that calling sync() only transmits the thirst information to the player it
    //belongs to, this is to save network traffic!
    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == provider;
    }

    public void sync() {
        HEATY.sync(provider);
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        temperature = tag.getInt("temperature");
        sync();
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt("temperature", temperature);
    }
}
