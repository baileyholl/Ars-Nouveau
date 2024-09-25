package com.hollingsworth.arsnouveau.common.command;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.AnimBlockSummon;
import com.hollingsworth.arsnouveau.common.entity.AnimHeadSummon;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;

public class SummonAnimHeadCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ars-skull").
                requires(sender -> {
                    return sender.hasPermission(4);
                })
                .then(Commands.argument("player_name", StringArgumentType.word())
                        .then(Commands.argument("duration", IntegerArgumentType.integer())
                                .then(Commands.argument("nbt", CompoundTagArgument.compoundTag())
                                        .then(Commands.argument("dropBlock", BoolArgumentType.bool()).executes(context ->{
                        return summonSkull(context.getSource(), String.valueOf(StringArgumentType.getString(context, "player_name")),
                                IntegerArgumentType.getInteger(context, "duration"),
                                CompoundTagArgument.getCompoundTag(context, "nbt"),
                                BoolArgumentType.getBool(context, "dropBlock"));
                }))))));
    }

    private static int summonSkull(CommandSourceStack source, String player_name, int duration, CompoundTag compoundTag, boolean dropSkull) {
        try {
            compoundTag.putString("id",ArsNouveau.prefix( "animated_head").toString());
            Entity entity = EntityType.loadEntityRecursive(compoundTag, source.getLevel(), (p_138828_) -> {
                p_138828_.moveTo(source.getPosition().x, source.getPosition().y, source.getPosition().z, p_138828_.getYRot(), p_138828_.getXRot());
                return p_138828_;
            });
            AnimHeadSummon animHeadSummon = (AnimHeadSummon) entity;
            animHeadSummon.blockState = Blocks.PLAYER_HEAD.defaultBlockState();
            animHeadSummon.head_data = AnimHeadSummon.getHeadTagFromName(player_name);
//        AnimHeadSummon animHeadSummon = new AnimHeadSummon(source.getLevel(), Blocks.PLAYER_HEAD.defaultBlockState(), AnimHeadSummon.getHeadTagFromName(player_name));
            animHeadSummon.setPos(source.getPosition());
            animHeadSummon.setTicksLeft(duration);
            animHeadSummon.getEntityData().set(AnimBlockSummon.CAN_WALK, true);
            animHeadSummon.getEntityData().set(AnimBlockSummon.AGE, 21);
            animHeadSummon.dropItem = dropSkull;
            source.getLevel().addFreshEntity(animHeadSummon);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 1;
    }
}
