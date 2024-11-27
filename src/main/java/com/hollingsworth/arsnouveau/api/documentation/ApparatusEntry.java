package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ApparatusEntry extends SinglePageWidget{
    RecipeHolder<EnchantingApparatusRecipe> apparatusRecipe;

    public ApparatusEntry(RecipeHolder<EnchantingApparatusRecipe> recipe, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.apparatusRecipe = recipe;
    }

    public static SinglePageCtor create(RecipeHolder<EnchantingApparatusRecipe> recipe){
        return (parent, x, y, width, height) -> new ApparatusEntry(recipe, parent, x, y, width, height);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        DocClientUtils.blit(guiGraphics, DocAssets.APPARATUS_RECIPE, x, y);
    }
}
