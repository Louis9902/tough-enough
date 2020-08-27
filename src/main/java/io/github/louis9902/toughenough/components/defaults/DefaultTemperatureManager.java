package io.github.louis9902.toughenough.components.defaults;

import io.github.louis9902.toughenough.components.HeatManager;
import io.github.louis9902.toughenough.temperature.TemperatureHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

import static io.github.louis9902.toughenough.ToughEnoughComponents.HEATY;
import static io.github.louis9902.toughenough.temperature.HeatManagerConstants.*;

public class DefaultHeatManager implements HeatManager {
    private static final int UPDATE_TICK = 20;

    private final PlayerEntity provider;

    private int ticks = 0;
    private int rateTicks = 0;

    private int rate = DEFAULT_RATE;
    private int target = DEFAULT_TARGET;
    private int temperature = TEMPERATURE_EQUILIBRIUM;

    public DefaultHeatManager(PlayerEntity provider) {
        this.provider = provider;
    }

    @Override
    public void update() {
        //no need to update values in creative or spectator mode
        if (provider.isCreative() || provider.isSpectator()) return;

        ticks++;
        rateTicks++;
        boolean shouldSync = false;

        if (ticks == UPDATE_TICK) {
            ticks = 0;

            int clampedRate = MathHelper.clamp(TemperatureHelper.calculatePlayerRate(provider), MIN_RATE, MAX_RATE);
            int clampedTarget = MathHelper.clamp(TemperatureHelper.calculatePlayerTarget(provider), MIN_TARGET, MAX_TARGET);

            //only sync data when one of the values has changed
            if (clampedRate != rate || clampedTarget != target) {
                rate = clampedRate;
                target = clampedTarget;
                shouldSync = true;
            }
        }

        //In case the rate gets reduced we don't want to run forever, so check against >= here
        if (rateTicks >= rate) {
            rateTicks = 0;
            //adjust temperature by one towards target
            if (temperature < target) {
                shouldSync = true;
                temperature++;
            } else if (temperature > target) {
                shouldSync = true;
                temperature--;
            }
        }

        if (shouldSync) sync();
    }

    //We override this so that calling sync() only transmits the thirst information to the player it
    //belongs to, this is to save network traffic!
    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == provider || player.getCameraEntity() == provider;
    }

    public void sync() {
        HEATY.sync(provider);
    }

    @Override
    public void writeToPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeInt(temperature);
        buf.writeInt(target);
        buf.writeInt(rate);
    }

    @Override
    public void readFromPacket(PacketByteBuf buf) {
        temperature = buf.readInt();
        target = buf.readInt();
        rate = buf.readInt();
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        //target and rate don't need to be saved in NBT as they can be reproduced with our functions easily
        temperature = tag.getInt("temperature");
        sync();
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt("temperature", temperature);
    }

    @Override
    public int getTemperature() {
        return temperature;
    }

    @Override
    public int getTarget() {
        return target;
    }

    @Override
    public int getRate() {
        return rate;
    }
}
