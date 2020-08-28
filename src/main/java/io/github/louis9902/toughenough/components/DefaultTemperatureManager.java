package io.github.louis9902.toughenough.components;

import io.github.louis9902.toughenough.api.temperature.TemperatureManager;
import io.github.louis9902.toughenough.api.debug.DebugMonitor;
import io.github.louis9902.toughenough.temperature.TemperatureHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

import static io.github.louis9902.toughenough.ToughEnoughComponents.TEMPERATURE_MANAGER;
import static io.github.louis9902.toughenough.temperature.HeatManagerConstants.*;

public class DefaultTemperatureManager implements TemperatureManager {

    private static final int UPDATE_TICK = 20;

    private final PlayerEntity provider;

    public final DebugMonitor targetMonitor = new DebugMonitor();
    public final DebugMonitor rateMonitor = new DebugMonitor();

    private int ticks = 0;
    private int rateTicks = 0;

    private int rate = DEFAULT_RATE;
    private int target = DEFAULT_TARGET;
    private int temperature = TEMPERATURE_EQUILIBRIUM;

    private boolean debugOutput = false;

    public DefaultTemperatureManager(PlayerEntity provider) {
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

            int clampedRate = MathHelper.clamp(TemperatureHelper.calcRateForPlayer(provider, rateMonitor), MIN_RATE, MAX_RATE);
            int clampedTarget = MathHelper.clamp(TemperatureHelper.calcTargetForPlayer(provider, targetMonitor), MIN_TARGET, MAX_TARGET);

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

    @Override
    public void writeToPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeInt(temperature);
        buf.writeBoolean(debugOutput);
        if (debugOutput) {
            buf.writeInt(target);
            buf.writeInt(rate);
            targetMonitor.encode(buf);
            rateMonitor.encode(buf);
        }
    }

    @Override
    public void readFromPacket(PacketByteBuf buf) {
        temperature = buf.readInt();
        debugOutput = buf.readBoolean();
        if (debugOutput) {
            target = buf.readInt();
            rate = buf.readInt();
            targetMonitor.decode(buf);
            rateMonitor.decode(buf);
        }
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

    @Override
    public boolean getDebug() {
        return debugOutput;
    }

    @Override
    public void setDebug(boolean value) {
        debugOutput = value;
        sync();
    }

    @Override
    public DebugMonitor getTargetMonitor() {
        return targetMonitor;
    }

    @Override
    public DebugMonitor getRateMonitor() {
        return rateMonitor;
    }

    //We override this so that calling sync() only transmits the thirst information to the player it
    //belongs to or to players spectating him, this is to save network traffic!
    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == provider || player.getCameraEntity() == provider;
    }

    public void sync() {
        TEMPERATURE_MANAGER.sync(provider);
    }
}
