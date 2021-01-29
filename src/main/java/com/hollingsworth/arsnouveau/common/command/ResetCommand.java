package com.hollingsworth.arsnouveau.common.command;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

public class ResetCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("ars-reset").
                requires(sender -> sender.hasPermissionLevel(2))
                .executes(context -> resetPlayers(context.getSource(), ImmutableList.of(context.getSource().assertIsEntity())))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .executes(context -> resetPlayers(context.getSource(), EntityArgument.getEntities(context, "targets")))));
    }

    private static int resetPlayers(CommandSource source, Collection<? extends Entity> entities) {
        for(Entity e : entities){
            if(!(e instanceof LivingEntity))
                continue;
            ManaCapability.getMana((LivingEntity) e).ifPresent(iMana -> {
                iMana.setBookTier(0);
                iMana.setGlyphBonus(0);
            });
        }
        source.sendFeedback(new TranslationTextComponent("ars_nouveau.reset.cleared"), true);
        return 1;
    }
}
