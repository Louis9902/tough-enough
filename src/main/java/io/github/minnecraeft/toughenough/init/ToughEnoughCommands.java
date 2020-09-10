package io.github.minnecraeft.toughenough.init;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.minnecraeft.toughenough.ToughEnoughComponents;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class ToughEnoughCommands {

    private final LiteralArgumentBuilder<ServerCommandSource> builder;

    private ToughEnoughCommands() {
        builder = literal("tough_enough");
    }

    public static void register() {
        ToughEnoughCommands commands = new ToughEnoughCommands();
        CommandRegistrationCallback.EVENT.register(commands::register);
    }

    private void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean server) {
        dispatcher.register(debug());
    }

    private LiteralArgumentBuilder<ServerCommandSource> debug() {
        return builder.then(literal("debug").then(argument("active", bool()).executes(context -> {
            PlayerEntity player = context.getSource().getPlayer();
            boolean active = getBool(context, "active");
            ToughEnoughComponents.DEBUGGER_MONITOR.get(player).setDebugging(active);
            return Command.SINGLE_SUCCESS;
        })));
    }

}
