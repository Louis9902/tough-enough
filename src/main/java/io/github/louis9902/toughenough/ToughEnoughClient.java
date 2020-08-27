package io.github.louis9902.toughenough;

import io.github.louis9902.toughenough.client.hud.ThirstHudRenderer;
import io.github.louis9902.toughenough.item.CanteenItem;
import io.github.louis9902.toughenough.item.JuiceItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ToughEnoughClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerModelPredicates();
        registerColorProviders();
        ThirstHudRenderer.register();
    }

    private static void registerModelPredicates() {
        FabricModelPredicateProviderRegistry.register(ToughEnough.identifier("empty"), (stack, world, entity) -> CanteenItem.hasFilling(stack) ? 0.0F : 1.0F);
        FabricModelPredicateProviderRegistry.register(ToughEnough.identifier("temperature"), new ThermometerPredicicateProvider());
    }

    private static void registerColorProviders() {
        ItemConvertible[] juices = ToughEnoughItems.JUICES.stream().map(ItemConvertible.class::cast).toArray(ItemConvertible[]::new);
        ColorProviderRegistry.ITEM.register(JuiceItem::colorForStack, juices);
    }

}
