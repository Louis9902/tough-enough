package io.github.louis9902.toughenough;

import io.github.louis9902.toughenough.client.hud.ThirstHudRenderer;
import io.github.louis9902.toughenough.item.CanteenItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;

@Environment(EnvType.CLIENT)
public class ToughEnoughClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerModelPredicates();
        ThirstHudRenderer.register();
    }

    private static void registerModelPredicates() {
        FabricModelPredicateProviderRegistry.register(ToughEnough.identifier("empty"), (stack, world, entity) -> CanteenItem.isDrinkable(stack) ? 0.0F : 1.0F);
    }

}
