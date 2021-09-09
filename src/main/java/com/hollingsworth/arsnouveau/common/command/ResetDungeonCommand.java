package com.hollingsworth.arsnouveau.common.command;

import com.hollingsworth.arsnouveau.common.dimension.dungeon.DungeonEvent;
import com.hollingsworth.arsnouveau.common.dimension.dungeon.DungeonManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.world.server.ServerWorld;

public class ResetDungeonCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("ars-reset-dungeon")
                .requires(sender -> sender.hasPermission(2)) // Op required
                                .executes(ResetDungeonCommand::resetDungeon)
        );
    }
    public static int resetDungeon(CommandContext<CommandSource> context) {
        System.out.println(DungeonManager.from((ServerWorld) context.getSource().getEntity().level).event);
        DungeonManager.from((ServerWorld) context.getSource().getEntity().level).event = new DungeonEvent((ServerWorld) context.getSource().getEntity().level);
        System.out.println(context.getSource().getEntity().level.dimension().location());
        return 1;
    }
}
