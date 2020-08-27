package io.github.louis9902.toughenough.components;

import dev.onyxstudios.cca.api.v3.component.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.ToIntFunction;

public interface TemperatureManager extends AutoSyncedComponent {
    int getTarget();

    int getTemperature();

    int getRate();

    void registerTargetCallback(ToIntFunction<PlayerEntity> func);

    void registerRateCallback(ToIntFunction<PlayerEntity> func);

    void removeTargetCallback(ToIntFunction<PlayerEntity> func);

    void removeRateCallback(ToIntFunction<PlayerEntity> func);

    void update();
}
