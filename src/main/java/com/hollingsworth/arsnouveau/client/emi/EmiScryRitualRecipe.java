package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.common.crafting.recipes.ScryRitualRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EmiScryRitualRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final ScryRitualRecipe recipe;

    public EmiScryRitualRecipe(ResourceLocation id, ScryRitualRecipe recipe) {
        this.id = id;
        this.recipe = recipe;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiArsNouveauPlugin.SCRY_RITUAL_CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(EmiIngredient.of(recipe.augment()));
    }

    @Override
    public List<EmiStack> getOutputs() {
        List<EmiStack> outputs = new ArrayList<>();

        recipe.highlight().left().ifPresent(block -> {
            for (Holder<Block> blockHolder : BuiltInRegistries.BLOCK.getTagOrEmpty(block.tag())) {
                outputs.add(EmiStack.of(blockHolder.value()));
            }
        });
        recipe.highlight().right().ifPresent(entity -> {
            for (Holder<EntityType<?>> entityTypeHolder : BuiltInRegistries.ENTITY_TYPE.getTagOrEmpty(entity.tag())) {
                SpawnEggItem item = SpawnEggItem.byId(entityTypeHolder.value());
                if (item != null) {
                    outputs.add(EmiStack.of(item));
                }
            }
        });

        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 120;
    }

    @Override
    public int getDisplayHeight() {
        return 24;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(EmiIngredient.of(this.getInputs()), 6, 4);
        widgets.addFillingArrow(48, 5, 40 * 50);
        widgets.addSlot(EmiIngredient.of(this.getOutputs()), 120 - 16 - 6, 4).recipeContext(this);
    }
}
