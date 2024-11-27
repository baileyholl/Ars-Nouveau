package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.GuiUtils;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class GlyphRecipeEntry extends SinglePageWidget{
    RecipeHolder<GlyphRecipe> recipe;
    ItemStack tooltipStack = ItemStack.EMPTY;
    int ticksElapsed = 0;
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
        drawHeader(Component.translatable("block.ars_nouveau.scribes_table"), guiGraphics, mouseX,mouseY, partialTick);
        DocClientUtils.blit(guiGraphics, DocAssets.SCRIBES_RECIPE,x + width/4, y + 28);
        if(recipe != null && recipe.value() != null) {
            List<Ingredient> ingredients = recipe.value().inputs;
            int degreePerInput = (int) (360F / ingredients.size());
            ticksElapsed = ClientInfo.ticksInGame;
            float currentDegree = ticksElapsed + partialTick;
            this.tooltipStack = ItemStack.EMPTY;
            for (Ingredient input : ingredients) {
                int renderX = x + 25;
                int renderY = y + 19;
                DocClientUtils.renderIngredientAtAngle(guiGraphics, renderX, renderY, currentDegree, input);
                double itemX = renderX + DocClientUtils.nextXAngle(currentDegree - 90, 32);
                double itemY = renderY + DocClientUtils.nextYAngle(currentDegree - 90, 32);
                if (GuiUtils.isMouseInRelativeRange(mouseX, mouseY, (int) itemX, (int) itemY, 16, 16)) {
                    this.tooltipStack = input.getItems()[ticksElapsed / 20 % input.getItems().length];
                }
                currentDegree += degreePerInput;
            }

        }
    }

    @Override
    public void gatherTooltips(List<Component> list) {
        super.gatherTooltips(list);
        if (!tooltipStack.isEmpty()) {
            list.addAll(tooltipStack.getTooltipLines(Item.TooltipContext.EMPTY, null, TooltipFlag.NORMAL));
        }
    }
}
