package io.github.louis9902.toughenough.components.defaults;

import io.github.louis9902.toughenough.components.HeatManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.function.ToIntFunction;

import static io.github.louis9902.toughenough.ToughEnoughComponents.HEATY;

public class DefaultHeatManager implements HeatManager {
    private static final int UPDATE_TICK = 20;
    private static final int MIN_TARGET = -10;
    private static final int MAX_TARGET = 10;
    private static final int DEFAULT_TARGET = 0;
    //100 ticks is 5 seconds
    private static final int MIN_RATE = 100;
    //2400 ticks is 2 minutes
    private static final int MAX_RATE = 2400;
    //600 ticks is 30 seconds
    private static final int DEFAULT_RATE = 600;
    private final ArrayList<ToIntFunction<PlayerEntity>> targetCallbacks = new ArrayList<>();
    private final ArrayList<ToIntFunction<PlayerEntity>> rateCallbacks = new ArrayList<>();
    private final PlayerEntity provider;
    private int ticks = 0;
    private int rateTicks = 0;
    private int target = DEFAULT_TARGET;
    private int rate = DEFAULT_RATE;
    private int temperature = 0;

    public DefaultHeatManager(PlayerEntity provider) {
        this.provider = provider;
        targetCallbacks.add(HeatManagerModifiers::biomeTarget);
        targetCallbacks.add(HeatManagerModifiers::timeTarget);
        targetCallbacks.add(HeatManagerModifiers::blockProximityTarget);
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

            int calcTarget = targetCallbacks.stream()
                    .mapToInt((item) -> item.applyAsInt(provider))
                    .sum();

            int calcRate = rateCallbacks.stream()
                    .mapToInt((item) -> item.applyAsInt(provider))
                    .reduce(DEFAULT_RATE, Integer::sum);

            int clampedRate = MathHelper.clamp(calcRate, MIN_RATE, MAX_RATE);
            int clampedTarget = MathHelper.clamp(calcTarget, MIN_TARGET, MAX_TARGET);

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

    @Override
    public void registerTargetCallback(ToIntFunction<PlayerEntity> func) {
        targetCallbacks.add(func);
    }

    @Override
    public void registerRateCallback(ToIntFunction<PlayerEntity> func) {
        rateCallbacks.add(func);
    }

    @Override
    public void removeTargetCallback(ToIntFunction<PlayerEntity> func) {
        targetCallbacks.remove(func);
    }

    @Override
    public void removeRateCallback(ToIntFunction<PlayerEntity> func) {
        rateCallbacks.remove(func);
    }
}
