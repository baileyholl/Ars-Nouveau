package com.hollingsworth.arsnouveau.common.command;

import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketExportDocs;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.server.command.ModIdArgument;

public class ExportDocsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ars-doc-export").
                requires(sender -> sender.hasPermission(2))
                .then(Commands.argument("modid", ModIdArgument.modIdArgument())
                        .executes(context -> {
                            Networking.sendToPlayerClient(new PacketExportDocs(context.getArgument("modid", String.class)), context.getSource().getPlayerOrException());
                            return 1;
                        })));
    }
}
