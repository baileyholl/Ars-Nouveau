package com.hollingsworth.arsnouveau.common.command;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import com.hollingsworth.arsnouveau.setup.reward.Rewards;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.Function;

public class AdoptCommand {
    private static SuggestionProvider<CommandSourceStack> getSuggestions(Function<Rewards.ContributorStarby, String> fn) {
        return (ctx, builder) -> {
            List<String> suggestions = Rewards.starbuncles.stream().map(fn).toList();
            return SharedSuggestionProvider.suggest(suggestions, builder);
        };
    }

    private static final SuggestionProvider<CommandSourceStack> BY_NAME = getSuggestions(starby -> starby.name);
    private static final SuggestionProvider<CommandSourceStack> BY_ADOPTER = getSuggestions(starby -> starby.adopter);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ars-adopted")
                .requires(sender -> sender.hasPermission(2))
                .then(
                        Commands.literal("by-name")
                                .then(
                                        Commands.argument("name", StringArgumentType.greedyString())
                                                .suggests(BY_NAME)
                                                .executes(ctx -> {
                                                    Rewards.ContributorStarby starby = getStarbuncle(starbuncle -> starbuncle.name, StringArgumentType.getString(ctx, "name"));
                                                    return spawnStarbuncle(ctx.getSource(), starby);
                                                })
                                )
                )
                .then(
                        Commands.literal("by-adopter")
                                .then(
                                        Commands.argument("adopter", StringArgumentType.greedyString())
                                                .suggests(BY_ADOPTER)
                                                .executes(ctx -> {
                                                    Rewards.ContributorStarby starby = getStarbuncle(starbuncle -> starbuncle.adopter, StringArgumentType.getString(ctx, "adopter"));
                                                    return spawnStarbuncle(ctx.getSource(), starby);
                                                })
                                )
                )
        );
    }

    private static Rewards.ContributorStarby getStarbuncle(Function<Rewards.ContributorStarby, String> selector, String match) {
        return Rewards.starbuncles.stream().filter(starbuncle -> selector.apply(starbuncle).equals(match)).findFirst().orElse(null);
    }

    private static int spawnStarbuncle(CommandSourceStack source, Rewards.ContributorStarby starbuncle) {
        if (starbuncle == null) {
            return 0;
        }

        Player player = source.getPlayer();
        if (player == null) return 0;

        Starbuncle starby = new Starbuncle(ModEntities.STARBUNCLE_TYPE.get(), player.level());
        starby.setPos(player.position());

        starby.setColor(starbuncle.color);
        starby.setCustomName(Component.literal(starbuncle.name));
        starby.data.bio = starbuncle.bio;
        starby.data.adopter = starbuncle.adopter;

        source.getLevel().addFreshEntity(starby);

        return 1;
    }
}
