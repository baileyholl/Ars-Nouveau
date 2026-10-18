package com.hollingsworth.arsnouveau.common.command;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.world.saved_data.ArcanoDimData;
import com.hollingsworth.nuggets.common.util.WorldHelpers;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class ArcanoResetCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ars-arcano-reset").
                requires(sender -> sender.hasPermission(2))
                .executes(context -> resetBoss(context.getSource())));
    }

    private static int resetBoss(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        if (!WorldHelpers.isOfWorldType(level, ArsNouveau.ARCANO_DIMENSION_TYPE_KEY)) {
            source.sendFailure(Component.literal("You are not standing in an Arcano boss dimension."));
            return 0;
        }
        ArcanoDimData.from(level).reset(level);
        source.sendSuccess(() -> Component.literal("Reset the Arcano boss fight for this dimension."), true);
        return 1;
    }
}
