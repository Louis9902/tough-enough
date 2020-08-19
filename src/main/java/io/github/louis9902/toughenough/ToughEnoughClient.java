package io.github.louis9902.toughenough;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.item.ElytraItem;

@Environment(EnvType.CLIENT)
public class ToughEnoughClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerModelPredicates();
    }

    private static void registerModelPredicates() {
        FabricModelPredicateProviderRegistry.register(ToughEnough.identifier("empty"), (stack, world, entity) -> {
            return ElytraItem.isUsable(stack) ? 0.0F : 1.0F;
        });
    }

}
