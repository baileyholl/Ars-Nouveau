package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.RecipeHolder;

public class GlyphRecipeEntry extends SinglePageWidget{
    RecipeHolder<GlyphRecipe> recipe;

    public GlyphRecipeEntry(RecipeHolder<GlyphRecipe> recipe, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.recipe = recipe;
    }

    public static SinglePageCtor create(RecipeHolder<GlyphRecipe> recipe){
        return (parent, x, y, width, height) -> new GlyphRecipeEntry(recipe, parent, x, y, width, height);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        DocClientUtils.blit(guiGraphics, DocAssets.RING,x, y);
    }
}
