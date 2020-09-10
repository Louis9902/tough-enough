package io.github.minnecraeft.toughenough;

import io.github.minnecraeft.toughenough.client.fluid.WaterTypeRenderer;
import io.github.minnecraeft.toughenough.client.hud.DebugHudRenderer;
import io.github.minnecraeft.toughenough.client.hud.TemperatureHudRenderer;
import io.github.minnecraeft.toughenough.client.hud.ThirstHudRenderer;
import io.github.minnecraeft.toughenough.client.item.ThermometerPredicicateProvider;
import io.github.minnecraeft.toughenough.client.screen.ClimatizerScreen;
import io.github.minnecraeft.toughenough.fluid.PurifiedWater;
import io.github.minnecraeft.toughenough.init.ToughEnoughFluids;
import io.github.minnecraeft.toughenough.init.ToughEnoughItems;
import io.github.minnecraeft.toughenough.init.ToughEnoughScreenHandlers;
import io.github.minnecraeft.toughenough.item.CanteenItem;
import io.github.minnecraeft.toughenough.item.JuiceItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.item.ItemConvertible;

@Environment(EnvType.CLIENT)
public class ToughEnoughClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(ToughEnoughScreenHandlers.CLIMATIZER_SCREEN_HANDLER, ClimatizerScreen::new);

        registerModelPredicates();
        registerColorProviders();

        ThirstHudRenderer.register();
        TemperatureHudRenderer.register();
        DebugHudRenderer.register();

        WaterTypeRenderer.register(ToughEnoughFluids.PURIFIED_WATER_STILL, ToughEnoughFluids.PURIFIED_WATER_FLOWING, ToughEnough.minecraft("water"), PurifiedWater.COLOR);

    }

    private static void registerModelPredicates() {
        FabricModelPredicateProviderRegistry.register(ToughEnough.identifier("empty"), (stack, world, entity) -> CanteenItem.hasFilling(stack) ? 0.0F : 1.0F);
        FabricModelPredicateProviderRegistry.register(ToughEnough.identifier("temperature"), new ThermometerPredicicateProvider());
    }

    private static void registerColorProviders() {
        // juice items need a color for their overlay
        ItemConvertible[] juices = ToughEnoughItems.JUICES.stream()
                .map(ItemConvertible.class::cast)
                .toArray(ItemConvertible[]::new);
        ColorProviderRegistry.ITEM.register(JuiceItem::colorForStack, juices);
        ColorProviderRegistry.ITEM.register((stack, index) -> index > 0 ? -1 : PurifiedWater.COLOR, ToughEnoughFluids.PURIFIED_WATER_BUCKET);
    }

}
