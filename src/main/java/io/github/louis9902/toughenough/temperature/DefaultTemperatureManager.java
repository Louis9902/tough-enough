package io.github.louis9902.toughenough.temperature;

import io.github.louis9902.toughenough.api.temperature.Climatization;
import io.github.louis9902.toughenough.api.temperature.TemperatureManager;
import io.github.louis9902.toughenough.api.debug.DebugMonitor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;

import static io.github.louis9902.toughenough.ToughEnoughComponents.TEMPERATURE_MANAGER;
import static io.github.louis9902.toughenough.temperature.TemperatureManagerConstants.*;

public class DefaultTemperatureManager implements TemperatureManager {

    private static final int UPDATE_TICK = 20;

    private final PlayerEntity provider;
    private final Map<Identifier, Climatization> climatization;

    public final DebugMonitor targetMonitor = new DebugMonitor();
    public final DebugMonitor rateMonitor = new DebugMonitor();

    private int update = 0;
    private int updateRate = 0;

    private int changeRate = DEFAULT_RATE;
    private int targetTemp = DEFAULT_TARGET;

    private int temperature = TEMPERATURE_EQUILIBRIUM;

    private boolean debugOutput = false;

    public DefaultTemperatureManager(PlayerEntity provider) {
        this.provider = provider;
        this.climatization = new HashMap<>();
    }

    @Override
    public void update() {
        // no need to update values in creative or spectator mode
        if (provider.isCreative() || provider.isSpectator()) return;

        // keep track if some data has been changed and update is necessary
        boolean sync = false;

        if (++update == UPDATE_TICK) {
            update = 0;

            int targetTemp = calcTargetTemp();
            int changeRate = calcChangeRate();

            // only sync data when one of the values has changed
            if (targetTemp != this.targetTemp || changeRate != this.changeRate) {
                this.changeRate = changeRate;
                this.targetTemp = targetTemp;
                sync = true;
            }
        }

        // in case the rate gets reduced check with >=
        boolean changeTemp = ++updateRate >= changeRate;

        for (Climatization effect : climatization.values()) {
            if (effect.getEndTime() < updateRate) {
                climatization.remove(effect.getIdentifier());
            }
        }

        if (changeTemp) {
            // reset rate 'time'
            updateRate = 0;

            // adjust temperature by one towards target
            if (temperature < targetTemp) {
                temperature++;
                sync = true;
            } else if (temperature > targetTemp) {
                temperature--;
                sync = true;
            }
        }

        if (sync) sync();
    }

    private int calcTargetTemp() {
        int temperature = TemperatureHelper.calcTargetForPlayer(provider, targetMonitor);
        temperature += climatization.values().stream().mapToInt(Climatization::getAmount).sum();
        return MathHelper.clamp(temperature, MIN_TEMPERATURE_TARGET, MAX_TEMPERATURE_TARGET);
    }

    private int calcChangeRate() {
        int rate = TemperatureHelper.calcRateForPlayer(provider, rateMonitor);
        return MathHelper.clamp(rate, MIN_CHANGE_RATE, MAX_CHANGE_RATE);
    }

    @Override
    public void writeToPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeInt(temperature);
        buf.writeBoolean(debugOutput);
        if (debugOutput) {
            buf.writeInt(targetTemp);
            buf.writeInt(changeRate);
            targetMonitor.encode(buf);
            rateMonitor.encode(buf);
        }
    }

    @Override
    public void readFromPacket(PacketByteBuf buf) {
        temperature = buf.readInt();
        debugOutput = buf.readBoolean();
        if (debugOutput) {
            targetTemp = buf.readInt();
            changeRate = buf.readInt();
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
        return targetTemp;
    }

    @Override
    public int getRate() {
        return changeRate;
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
