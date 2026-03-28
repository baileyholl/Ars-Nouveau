package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageWidget;
import com.hollingsworth.arsnouveau.api.documentation.export.DocExporter;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.setup.registry.RegistryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import java.util.List;

public class CraftingEntry extends SinglePageWidget {

    public RecipeHolder<CraftingRecipe> recipe1;
    public RecipeHolder<CraftingRecipe> recipe2;
    public Component description;

    public CraftingEntry(RecipeHolder<CraftingRecipe> recipe1, RecipeHolder<CraftingRecipe> recipe2, Component description, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.recipe1 = recipe1;
        this.recipe2 = recipe2;
        this.description = description;
    }

    public CraftingEntry(RecipeHolder<CraftingRecipe> recipe1, BaseDocScreen parent, int x, int y, int width, int height) {
        this(recipe1, null, null, parent, x, y, width, height);
    }

    public static SinglePageCtor create(RecipeHolder<CraftingRecipe> recipe1, Component description) {
        return (parent, x, y, width, height) -> new CraftingEntry(recipe1, null, description, parent, x, y, width, height);
    }

    public static SinglePageCtor create(RecipeHolder<CraftingRecipe> recipe1) {
        return (parent, x, y, width, height) -> new CraftingEntry(recipe1, parent, x, y, width, height);
    }

    public static SinglePageCtor create(RecipeHolder<CraftingRecipe> recipe1, RecipeHolder<CraftingRecipe> recipe2) {
        return (parent, x, y, width, height) -> new CraftingEntry(recipe1, recipe2, null, parent, x, y, width, height);
    }

    @SuppressWarnings("unchecked")
    public static SinglePageCtor create(ItemLike item1, ItemLike item2) {
        Level level = ArsNouveau.proxy.getClientWorld();
        RecipeManager manager = ((net.minecraft.world.item.crafting.RecipeManager)level.recipeAccess());
        RecipeHolder<CraftingRecipe> recipe1 = (RecipeHolder<CraftingRecipe>) manager
                .byKey(ResourceKey.create(Registries.RECIPE, RegistryHelper.getRegistryName(item1.asItem()))).orElse(null);
        RecipeHolder<CraftingRecipe> recipe2 = (RecipeHolder<CraftingRecipe>) manager
                .byKey(ResourceKey.create(Registries.RECIPE, RegistryHelper.getRegistryName(item2.asItem()))).orElse(null);
        return (parent, x, y, width, height) -> new CraftingEntry(recipe1, recipe2, null, parent, x, y, width, height);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        DocClientUtils.drawHeader(Component.translatable("block.minecraft.crafting_table"), guiGraphics, x, y, width, mouseX, mouseY, partialTick);
        if (recipe1 == null && recipe2 == null) {
            return;
        }
        if (recipe1 != null) {
            drawCraftingGrid(guiGraphics, x + 14, y + 16, mouseX, mouseY, recipe1);
        }

        if (recipe2 != null) {
            drawCraftingGrid(guiGraphics, x + 14, y + 84, mouseX, mouseY, recipe2);
        }
    }

    public void drawCraftingGrid(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, RecipeHolder<CraftingRecipe> recipe) {
        // 1.21.11: getResultItem() and getIngredients() removed from Recipe interface.
        // Use assemble(EMPTY, registryAccess) for result; use placementInfo() for ingredients.
        ItemStack outputStack = recipe.value().assemble(CraftingInput.EMPTY, Minecraft.getInstance().level.registryAccess());
        PlacementInfo info = recipe.value().placementInfo();
        java.util.List<Ingredient> flatIngredients = info.ingredients();
        it.unimi.dsi.fastutil.ints.IntList slotMap = info.slotsToIngredientIndex();
        DocClientUtils.blit(guiGraphics, DocAssets.CRAFTING_ENTRY_1, x, y);
        int row = 0;
        for (int i = 0; i < slotMap.size(); i++) {
            int col = i % 3;
            if (col == 0 && i != 0) {
                row++;
            }
            int idx = slotMap.getInt(i);
            if (idx == PlacementInfo.EMPTY_SLOT) continue;
            Ingredient ingredient = flatIngredients.get(idx);
            int renderX = x + 3 + (col * 21);
            int renderY = y + 3 + (row * 21);
            this.setTooltipIfHovered(DocClientUtils.renderIngredient(guiGraphics, renderX, renderY, mouseX, mouseY, ingredient));
        }

        this.setTooltipIfHovered(DocClientUtils.renderItemStack(guiGraphics, x + 86, y + 24, mouseX, mouseY, outputStack));
    }

    @Override
    public void addExportProperties(JsonObject object) {
        super.addExportProperties(object);
        if (recipe1 != null) {
            object.addProperty(DocExporter.RECIPE_PROPERTY, recipe1.id().toString());
        }
        if (recipe2 != null) {
            object.addProperty(DocExporter.RECIPE2_PROPERTY, recipe2.id().toString());
        }
        if (description != null) {
            object.addProperty(DocExporter.DESCRIPTION_PROPERTY, description.getString());
        }
    }
}
