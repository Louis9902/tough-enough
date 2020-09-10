package io.github.minnecraeft.toughenough.temperature;

import io.github.minnecraeft.toughenough.ToughEnoughComponents;
import io.github.minnecraeft.toughenough.api.debug.DebugMonitor;
import io.github.minnecraeft.toughenough.api.temperature.TemperatureConstants;
import io.github.minnecraeft.toughenough.api.temperature.TemperatureManager;
import io.github.minnecraeft.toughenough.api.temperature.TemperatureModifier;
import io.github.minnecraeft.toughenough.api.temperature.TemperatureModifiers;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

public class DefaultTemperatureManager implements TemperatureManager {

    public static final String[] DEBUG_TARGET_MODIFIERS = {"temperature_manager", "target_modifiers"};
    public static final String[] DEBUG_RATE_MODIFIERS = {"temperature_manager", "rate_modifiers"};

    private static final int UPDATE_TICK = 20;

    private final PlayerEntity provider;
    private final Map<Identifier, TemperatureModifier.Temporary> modifiersTarget;
    private final Map<Identifier, TemperatureModifier.Temporary> modifiersRate;

    private int update = 0;
    private int updateRate = 0;

    private int changeRate = TemperatureConstants.DEFAULT_CHANGE_RATE;
    private int targetTemp = TemperatureConstants.DEFAULT_TEMPERATURE_TARGET;

    private int temperature = TemperatureConstants.TEMPERATURE_EQUILIBRIUM;

    public DefaultTemperatureManager(PlayerEntity player) {
        provider = player;
        modifiersTarget = new HashMap<>();
        modifiersRate = new HashMap<>();
    }

    @Override
    public void addModifierTarget(Identifier identifier, int amount, int duration) {
        addModifier(modifiersTarget, identifier, amount, duration);
    }

    @Override
    public void addModifierRate(Identifier identifier, int amount, int duration) {
        addModifier(modifiersRate, identifier, amount, duration);
    }

    private static void addModifier(Map<Identifier, TemperatureModifier.Temporary> map, Identifier identifier, int amount, int duration) {
        TemperatureModifier.Temporary modifier = map.get(identifier);
        if (modifier == null) {
            modifier = new TemperatureModifier.Temporary(identifier, amount, duration);
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

        final DebugMonitor monitor = ToughEnoughComponents.DEBUGGER_MONITOR.get(provider);

        // keep track if some data has been changed and update is necessary
        boolean sync = false;

        ++update;
        ++updateRate;

        if (update == UPDATE_TICK) {
            update = 0;

            updateAndRemove(modifiersTarget.values().iterator(), modifiersTarget::remove);
            updateAndRemove(modifiersRate.values().iterator(), modifiersRate::remove);

            if (monitor.isDebugging())
                monitor.section("TemperatureManager").putInt("Target", targetTemp);
            int targetTemp = calcTargetTemp(monitor);

            if (monitor.isDebugging())
                monitor.section("TemperatureManager").putInt("Rate", changeRate);
            int changeRate = calcChangeRate(monitor);

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

    // Necessary because of fail fast requirement for iterating and removing
    private static void updateAndRemove(Iterator<TemperatureModifier.Temporary> iterator, Consumer<Identifier> consumer) {
        while (iterator.hasNext()) {
            TemperatureModifier.Temporary modifier = iterator.next();
            if (modifier.update()) {
                consumer.accept(modifier.getIdentifier());
            }
        }
    }

    private int calcTargetTemp(DebugMonitor monitor) {
        int temperature = TemperatureModifiers.calcTargetForPlayer(provider);
        temperature += modifiersTarget.values().stream().mapToInt(modifier -> {
            int amount = modifier.getAmount();
            if (monitor.isDebugging()) {
                Identifier identifier = modifier.getIdentifier();
                monitor.section("TemperatureManager", "TargetModifiers", "Player").putInt(identifier.getPath(), amount);
            }
            return amount;
        }).sum();
        return MathHelper.clamp(temperature, TemperatureConstants.MIN_TEMPERATURE_TARGET, TemperatureConstants.MAX_TEMPERATURE_TARGET);
    }

    private int calcChangeRate(DebugMonitor monitor) {
        int rate = TemperatureModifiers.calcRateForPlayer(provider);
        rate += modifiersRate.values().stream().mapToInt(modifier -> {
            int amount = modifier.getAmount();
            if (monitor.isDebugging()) {
                Identifier identifier = modifier.getIdentifier();
                monitor.section("TemperatureManager", "RateModifiers", "Player").putInt(identifier.getPath(), amount);
            }
            return amount;
        }).sum();
        return MathHelper.clamp(rate, TemperatureConstants.MIN_CHANGE_RATE, TemperatureConstants.MAX_CHANGE_RATE);
    }

    @Override
    public void writeToPacket(PacketByteBuf buf, ServerPlayerEntity recipient, int code) {
        buf.writeInt(temperature);
    }

    @Override
    public void readFromPacket(PacketByteBuf buf) {
        temperature = buf.readInt();
    }

    @Override
    public void readFromNbt(CompoundTag compound) {
        temperature = compound.getInt("Temperature");

        modifiersTarget.clear();
        compound.getList("TargetModifiers", NbtType.COMPOUND).stream()
                .map(CompoundTag.class::cast)
                .map(TemperatureModifier.Temporary::new)
                .forEach(modifier -> modifiersTarget.put(modifier.getIdentifier(), modifier));

        modifiersRate.clear();
        compound.getList("RateModifiers", NbtType.COMPOUND).stream()
                .map(CompoundTag.class::cast)
                .map(TemperatureModifier.Temporary::new)
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
        for (TemperatureModifier.Temporary value : modifiersTarget.values()) {
            modifiers.add(index++, value.encode());
        }
        compound.put("TargetModifiers", modifiers);

        modifiers = new ListTag();
        index = 0;
        for (TemperatureModifier.Temporary value : modifiersRate.values()) {
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

    //We override this so that calling sync() only transmits the thirst information to the player it
    //belongs to or to players spectating him, this is to save network traffic!
    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player, int code) {
        return player == provider || player.getCameraEntity() == provider;
    }

    public void sync() {
        ToughEnoughComponents.TEMPERATURE_MANAGER.sync(provider);
    }
}
