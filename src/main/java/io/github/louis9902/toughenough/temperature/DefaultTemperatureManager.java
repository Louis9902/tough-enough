package io.github.louis9902.toughenough.temperature;

import io.github.louis9902.toughenough.api.temperature.Modifier;
import io.github.louis9902.toughenough.api.temperature.TemperatureManager;
import io.github.louis9902.toughenough.api.debug.DebugMonitor;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
    private final Map<Identifier, Modifier.Temporary> modifiersTarget;
    private final Map<Identifier, Modifier.Temporary> modifiersRate;

    public final DebugMonitor targetMonitor = new DebugMonitor();
    public final DebugMonitor rateMonitor = new DebugMonitor();

    private int update = 0;
    private int updateRate = 0;

    private int changeRate = DEFAULT_CHANGE_RATE;
    private int targetTemp = DEFAULT_TEMPERATURE_TARGET;

    private int temperature = TEMPERATURE_EQUILIBRIUM;

    private boolean debugOutput = false;

    public DefaultTemperatureManager(PlayerEntity provider) {
        this.provider = provider;
        this.modifiersTarget = new HashMap<>();
        this.modifiersRate = new HashMap<>();
    }

    @Override
    public void addModifierTarget(Identifier identifier, int amount, int duration) {
        addModifier(modifiersTarget, identifier, amount, duration);
    }

    @Override
    public void addModifierRate(Identifier identifier, int amount, int duration) {
        addModifier(modifiersRate, identifier, amount, duration);
    }

    private static void addModifier(Map<Identifier, Modifier.Temporary> map, Identifier identifier, int amount, int duration) {
        Modifier.Temporary modifier = map.get(identifier);
        if (modifier == null) {
            modifier = new Modifier.Temporary(identifier, amount, duration);
            map.put(identifier, modifier);
        } else {
            modifier.setAmount(amount);
            modifier.setDuration(duration);
        }
    }

    @Override
    public void update() {
        // no need to update values in creative or spectator mode
        if (provider.isCreative() || provider.isSpectator()) return;

        // keep track if some data has been changed and update is necessary
        boolean sync = false;

        ++update;
        ++updateRate;

        if (update == UPDATE_TICK) {
            update = 0;

            modifiersTarget.values().stream()
                    .filter(Modifier.Temporary::update)
                    .map(Modifier.Temporary::getIdentifier)
                    .forEach(modifiersTarget::remove);

            modifiersRate.values().stream()
                    .filter(Modifier.Temporary::update)
                    .map(Modifier.Temporary::getIdentifier)
                    .forEach(modifiersRate::remove);

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
        if (updateRate >= changeRate) {
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
        temperature += modifiersTarget.values().stream().mapToInt(Modifier.Temporary::getAmount).sum();
        return MathHelper.clamp(temperature, MIN_TEMPERATURE_TARGET, MAX_TEMPERATURE_TARGET);
    }

    private int calcChangeRate() {
        int rate = TemperatureHelper.calcRateForPlayer(provider, rateMonitor);
        rate += modifiersRate.values().stream().mapToInt(Modifier.Temporary::getAmount).sum();
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
    public void readFromNbt(CompoundTag compound) {
        temperature = compound.getInt("Temperature");

        modifiersTarget.clear();
        compound.getList("TargetModifiers", NbtType.COMPOUND).stream()
                .map(CompoundTag.class::cast)
                .map(Modifier.Temporary::new)
                .forEach(modifier -> modifiersTarget.put(modifier.getIdentifier(), modifier));

        modifiersRate.clear();
        compound.getList("RateModifiers", NbtType.COMPOUND).stream()
                .map(CompoundTag.class::cast)
                .map(Modifier.Temporary::new)
                .forEach(modifier -> modifiersRate.put(modifier.getIdentifier(), modifier));

        sync();
    }

    @Override
    public void writeToNbt(CompoundTag compound) {
        compound.putInt("Temperature", temperature);

        ListTag modifiers;
        int index;

        modifiers = new ListTag();
        index = 0;
        for (Modifier.Temporary value : modifiersTarget.values()) {
            modifiers.add(index++, value.encode());
        }
        compound.put("TargetModifiers", modifiers);

        modifiers = new ListTag();
        index = 0;
        for (Modifier.Temporary value : modifiersRate.values()) {
            modifiers.add(index++, value.encode());
        }
        compound.put("RateModifiers", modifiers);
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
