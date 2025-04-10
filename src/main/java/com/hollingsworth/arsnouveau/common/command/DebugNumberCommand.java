package com.hollingsworth.arsnouveau.common.command;

import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketToggleDebug;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class DebugNumberCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ars-debug").
                requires(sender -> sender.hasPermission(1))
                .then(Commands.literal("on").executes(context -> toggleDebugNumbers(context.getSource(), true)))
                .then(Commands.literal("off").executes(context -> toggleDebugNumbers(context.getSource(), false))));
    }

    private static int toggleDebugNumbers(CommandSourceStack source, boolean enable) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return 0;
        }
        Networking.sendToPlayerClient(new PacketToggleDebug(enable), player);
        return 1;
    }


}
