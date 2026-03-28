package com.hollingsworth.arsnouveau.common.command;

import com.hollingsworth.arsnouveau.api.registry.CasterTomeRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CasterTomeData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Optional;

public class AddTomeCommand {
    private static final SuggestionProvider<CommandSourceStack> sugg = (ctx, builder) -> SharedSuggestionProvider.suggestResource(CasterTomeRegistry.getTomeData().stream().map(r -> r.id().identifier()), builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ars-tome").
                requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("tome", IdentifierArgument.id())
                        .suggests(sugg)
                        .executes(context -> spawnTome(context.getSource(), String.valueOf(IdentifierArgument.getId(context, "tome"))))));
    }

    private static int spawnTome(CommandSourceStack source, String tome) {
        Optional<RecipeHolder<CasterTomeData>> data = CasterTomeRegistry.getTomeData().stream().filter(t -> t.id().toString().equals(tome)).findFirst();
        if (data.isPresent() && source.getPlayer() != null) {
            CasterTomeData tomeData = data.get().value();
            Item tomeItem = BuiltInRegistries.ITEM.get(Identifier.tryParse(tomeData.tomeType().toString())).map(h -> h.value()).orElse(net.minecraft.world.item.Items.AIR);
            source.getPlayer().addItem(CasterTomeData.makeTome(tomeItem, tomeData.spell(), tomeData.flavorText()));
        }
        return 1;
    }
}
