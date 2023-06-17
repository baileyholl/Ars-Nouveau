package com.hollingsworth.arsnouveau.common.command;

import com.hollingsworth.arsnouveau.common.entity.AnimBlockSummon;
import com.hollingsworth.arsnouveau.common.entity.AnimHeadSummon;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.level.block.Blocks;

public class SummonAnimHeadCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ars-skull").
                requires(sender -> {
                    return sender.hasPermission(4);
                })
                .then(Commands.argument("player_name", StringArgumentType.word()).then(Commands.argument("duration", IntegerArgumentType.integer()).executes(context ->{
                        return summonSkull(context.getSource(), String.valueOf(StringArgumentType.getString(context, "player_name")), IntegerArgumentType.getInteger(context, "duration"));
                }))));
    }

    private static int summonSkull(CommandSourceStack source, String player_name, int duration) {
        AnimHeadSummon animHeadSummon = new AnimHeadSummon(source.getLevel(), Blocks.PLAYER_HEAD.defaultBlockState(), AnimHeadSummon.getHeadTagFromName(player_name));
        animHeadSummon.setPos(source.getPosition());
        animHeadSummon.setTicksLeft(duration);
        animHeadSummon.getEntityData().set(AnimBlockSummon.CAN_WALK, true);
        animHeadSummon.getEntityData().set(AnimBlockSummon.AGE, 21);
        source.getLevel().addFreshEntity(animHeadSummon);
        return 1;
    }
}
