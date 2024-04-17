package com.hollingsworth.arsnouveau.common.command;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class LearnGlyphCommand {
    private static final SuggestionProvider<CommandSourceStack> sugg = (ctx, builder) -> SharedSuggestionProvider.suggestResource(GlyphRegistry.getSpellpartMap().keySet(), builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ars-glyph")
                .requires(sender -> sender.hasPermission(2))
                .executes(context -> learnGlyph(context.getSource(), List.of(context.getSource().getPlayer()), null))
                .then(
                        Commands.argument("targets", EntityArgument.players())
                                .executes(context -> learnGlyph(context.getSource(), EntityArgument.getPlayers(context, "targets"), null))
                                .then(Commands.argument("glyph", ResourceLocationArgument.id())
                                        .suggests(sugg)
                                        .executes(context -> learnGlyph(context.getSource(), EntityArgument.getPlayers(context, "targets"), ResourceLocationArgument.getId(context, "glyph"))))
                )

        );
    }

    private static int learnGlyph(CommandSourceStack source, Collection<ServerPlayer> players, @Nullable ResourceLocation glyph) {
        if (source.getPlayer() == null) return 0;

        for (ServerPlayer player : players) {
            IPlayerCap playerCap = CapabilityRegistry.getPlayerDataCap(player).orElse(null);

            if (glyph == null) {
                if (playerCap == null) continue;
                playerCap.setKnownGlyphs(GlyphRegistry.getSpellpartMap().values());
                player.sendSystemMessage(Component.literal("Unlocked all glyphs"));
            } else {
                AbstractSpellPart spellPart = GlyphRegistry.getSpellPart(glyph);
                boolean learned = playerCap.unlockGlyph(spellPart);
                if (learned) {
                    player.sendSystemMessage(Component.literal("Unlocked " + spellPart.getName()));
                } else {
                    player.sendSystemMessage(Component.literal("Glyph already known"));
                }
            }

            CapabilityRegistry.EventHandler.syncPlayerCap(player);
        }

        return 1;
    }
}
