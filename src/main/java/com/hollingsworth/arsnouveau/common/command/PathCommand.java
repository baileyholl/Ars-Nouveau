package com.hollingsworth.arsnouveau.common.command;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketTogglePathing;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;

public class PathCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ars-pathing").
                requires(sender -> sender.hasPermission(2))
                .executes(context -> setPathing(context.getSource(), ImmutableList.of(context.getSource().getEntityOrException()))));
    }

    private static int setPathing(CommandSourceStack source, ImmutableList<? extends Entity> of) {
        Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) of.get(0)),
                new PacketTogglePathing());
        return 1;
    }

}
