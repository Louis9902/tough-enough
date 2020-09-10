package io.github.minnecraeft.toughenough;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import io.github.minnecraeft.toughenough.api.debug.DebugMonitor;
import io.github.minnecraeft.toughenough.api.temperature.TemperatureManager;
import io.github.minnecraeft.toughenough.api.thirst.Drink;
import io.github.minnecraeft.toughenough.api.thirst.ThirstManager;
import io.github.minnecraeft.toughenough.debug.DefaultDebugMonitor;
import io.github.minnecraeft.toughenough.item.DrinkItem;
import io.github.minnecraeft.toughenough.temperature.DefaultTemperatureManager;
import io.github.minnecraeft.toughenough.thirst.DefaultThirstManager;
import io.github.minnecraeft.toughenough.thirst.ThirstCompat;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ToughEnoughComponents implements ItemComponentInitializer, EntityComponentInitializer {

    public static final ComponentKey<Drink> DRINKABLE = ComponentRegistryV3.INSTANCE.getOrCreate(ToughEnough.identifier("drinkable"), Drink.class);

    public static final ComponentKey<ThirstManager> THIRST_MANAGER;
    public static final ComponentKey<TemperatureManager> TEMPERATURE_MANAGER;
    public static final ComponentKey<DebugMonitor> DEBUGGER_MONITOR;

    static {
        DEBUGGER_MONITOR = ComponentRegistryV3.INSTANCE.getOrCreate(ToughEnough.identifier("debug_monitor"), DebugMonitor.class);
        THIRST_MANAGER = ComponentRegistryV3.INSTANCE.getOrCreate(ToughEnough.identifier("thirst_manager"), ThirstManager.class);
        TEMPERATURE_MANAGER = ComponentRegistryV3.INSTANCE.getOrCreate(ToughEnough.identifier("temperature_manager"), TemperatureManager.class);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        //Heat and Thirst will only be saved when player returns from end, not when dying
        registry.registerForPlayers(DEBUGGER_MONITOR, DefaultDebugMonitor::new, RespawnCopyStrategy.LOSSLESS_ONLY);
        registry.registerForPlayers(THIRST_MANAGER, DefaultThirstManager::new, RespawnCopyStrategy.LOSSLESS_ONLY);
        registry.registerForPlayers(TEMPERATURE_MANAGER, DefaultTemperatureManager::new, RespawnCopyStrategy.LOSSLESS_ONLY);
    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
        registry.registerFor(DrinkItem.class::isInstance, DRINKABLE, (item, stack) -> ((DrinkItem) item).componentToAttach(stack));
        for (Map.Entry<Identifier, Drink> entry : ThirstCompat.DRINKS.entrySet()) {
            registry.registerFor(entry.getKey(), DRINKABLE, (item, stack) -> entry.getValue());
        }
    }
}
