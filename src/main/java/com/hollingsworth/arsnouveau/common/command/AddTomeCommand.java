package com.hollingsworth.arsnouveau.common.command;

import com.hollingsworth.arsnouveau.api.registry.CasterTomeRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CasterTomeData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Optional;

public class AddTomeCommand {
    private static final SuggestionProvider<CommandSourceStack> sugg = (ctx, builder) -> SharedSuggestionProvider.suggestResource(CasterTomeRegistry.getTomeData().stream().map(RecipeHolder::id), builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ars-tome").
                requires(sender -> sender.hasPermission(2))
                .then(Commands.argument("tome", ResourceLocationArgument.id())
                        .suggests(sugg)
                        .executes(context -> spawnTome(context.getSource(), String.valueOf(ResourceLocationArgument.getId(context, "tome"))))));
    }

    private static int spawnTome(CommandSourceStack source, String tome) {
        Optional<RecipeHolder<CasterTomeData>> data = CasterTomeRegistry.getTomeData().stream().filter(t -> t.id().toString().equals(tome)).findFirst();
        if (data.isPresent() && source.getPlayer() != null) {
            source.getPlayer().addItem(data.get().value().getResultItem(source.getLevel().registryAccess()).copy());
        }
        return 1;
    }
}
