package io.github.louis9902.toughenough;

import com.mojang.brigadier.Command;
import io.github.louis9902.toughenough.client.hud.ThirstHudRenderer;
import io.github.louis9902.toughenough.client.modelpredicicates.ThermometerPredicicateProvider;
import io.github.louis9902.toughenough.init.ToughEnoughItems;
import io.github.louis9902.toughenough.item.CanteenItem;
import io.github.louis9902.toughenough.item.JuiceItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static io.github.louis9902.toughenough.ToughEnoughComponents.HEATY;
import static io.github.louis9902.toughenough.ToughEnoughComponents.THIRSTY;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@Environment(EnvType.CLIENT)
public class ToughEnoughClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerModelPredicates();
        registerColorProviders();
        ThirstHudRenderer.register();

        //TODO move this to a proper place sometime
        CommandRegistrationCallback.EVENT.register((dispatcher, ded) -> {
            dispatcher.register(literal("tough_enough")
                    .then(literal("debug")
                            .then(argument("bool", bool())
                                    .executes(ctx -> {
                                        PlayerEntity player = ctx.getSource().getPlayer();
                                        boolean arg = getBool(ctx, "bool");
                                        HEATY.get(player).setDebug(arg);
                                        THIRSTY.get(player).setDebug(arg);
                                        return Command.SINGLE_SUCCESS;
                                    }))));
        });
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
