package io.github.louis9902.toughenough;

import io.github.louis9902.toughenough.client.HotBarDrawer;
import io.github.louis9902.toughenough.item.Canteen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;

@Environment(EnvType.CLIENT)
public class ToughEnoughClient implements ClientModInitializer {

    private HotBarDrawer hotbar = new HotBarDrawer();
    @Override
    public void onInitializeClient() {
        registerModelPredicates();
    }

    private static void registerModelPredicates() {
        FabricModelPredicateProviderRegistry.register(ToughEnough.identifier("empty"), (stack, world, entity) -> Canteen.isUsable(stack) ? 0.0F : 1.0F);
    }

}
