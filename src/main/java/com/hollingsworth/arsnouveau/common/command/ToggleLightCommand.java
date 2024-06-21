package com.hollingsworth.arsnouveau.common.command;

import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketToggleLight;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class ToggleLightCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ars-light").
                requires(sender -> sender.hasPermission(0))
                .then(Commands.literal("on").executes(context -> resetPlayers(context.getSource(), true)))
                .then(Commands.literal("off").executes(context -> resetPlayers(context.getSource(), false))));
    }

    private static int resetPlayers(CommandSourceStack source, boolean enable) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return 1;
        }
        Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketToggleLight(enable));
        String path = enable ? "ars_nouveau.lights_on" : "ars_nouveau.lights_off";
        player.sendSystemMessage(Component.translatable(path, enable));
        return 1;
    }
}
