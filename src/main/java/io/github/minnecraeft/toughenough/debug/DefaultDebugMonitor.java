package io.github.minnecraeft.toughenough.debug;

import io.github.minnecraeft.toughenough.ToughEnoughComponents;
import io.github.minnecraeft.toughenough.api.debug.DebugMonitor;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class DefaultDebugMonitor implements DebugMonitor {

    private final PlayerEntity provider;

    private final CompoundTag compound;
    private boolean debugging;

    public DefaultDebugMonitor(PlayerEntity player) {
        provider = player;
        compound = new CompoundTag();
    }

    @Override
    public boolean isDebugging() {
        return debugging;
    }

    @Override
    public void setDebugging(boolean active) {
        debugging = active;
    }

    @Override
    public CompoundTag section(String name) {
        CompoundTag current = compound;
        if (current.contains(name, NbtType.COMPOUND)) {
            current = current.getCompound(name);
        } else {
            CompoundTag compound = new CompoundTag();
            current.put(name, compound);
            current = compound;
        }
        return current;
    }

    @Override
    public CompoundTag section(String... names) {
        CompoundTag current = compound;
        for (String name : names) {
            if (current.contains(name, NbtType.COMPOUND)) {
                current = current.getCompound(name);
            } else {
                CompoundTag compound = new CompoundTag();
                current.put(name, compound);
                current = compound;
            }
        }
        return current;
    }

    @Override
    public void checkForSync() {
        if (isDebugging()) {
            ToughEnoughComponents.DEBUGGER_MONITOR.sync(provider);
        }
    }

    @Override
    public void readFromNbt(CompoundTag compound) {
        debugging = compound.getBoolean("Debugging");
    }

    @Override
    public void writeToNbt(CompoundTag compound) {
        compound.putBoolean("Debugging", isDebugging());
    }

    @Override
    public void writeToPacket(PacketByteBuf buf, ServerPlayerEntity recipient, int code) {
        buf.writeBoolean(debugging);
        if (isDebugging()) {
            buf.writeCompoundTag(compound);
        }
    }

    @Override
    public void readFromPacket(PacketByteBuf buf) {
        debugging = buf.readBoolean();
        if (isDebugging()) {
            CompoundTag source = buf.readCompoundTag();
            compound.copyFrom(source);
        }
    }

}
