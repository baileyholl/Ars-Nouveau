package com.hollingsworth.arsnouveau.client.patchouli;

import com.hollingsworth.arsnouveau.api.recipe.GlyphPressRecipe;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class GlyphPressProcessor implements IComponentProcessor {

    GlyphPressRecipe recipe;
    @Override
    public void setup(IVariableProvider variables) {
        RecipeManager manager = Minecraft.getInstance().world.getRecipeManager();
        String recipeID = variables.get("recipe").asString();
        recipe = (GlyphPressRecipe) manager.getRecipe(new ResourceLocation(recipeID)).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public IVariable process(String s) {
        if(s.equals("clay_type"))
            return IVariable.from(recipe.getClay());
        if(s.equals("reagent"))
            return IVariable.from(recipe.reagent);
        if(s.equals("tier"))
            return IVariable.wrap(I18n.format("ars_nouveau.spell_tier." + recipe.tier.toString().toLowerCase()));
        if(s.equals("mana_cost") ){
            if(recipe.output.getItem() instanceof Glyph){
                int cost =  ((Glyph) recipe.output.getItem()).spellPart.getManaCost();
                String costLang = "";
                if(cost == 0)
                    costLang = I18n.format("ars_nouveau.mana_cost.none");
                if(cost < 20)
                    costLang = I18n.format("ars_nouveau.mana_cost.low");
                if(cost < 50)
                    costLang = I18n.format("ars_nouveau.mana_cost.medium");
                if(cost >= 50)
                    costLang = I18n.format("ars_nouveau.mana_cost.high");
                return IVariable.wrap(costLang);
            }
            return IVariable.wrap("");
        }
        return null;
    }
}
