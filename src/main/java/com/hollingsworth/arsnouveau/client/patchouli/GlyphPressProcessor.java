package com.hollingsworth.arsnouveau.client.patchouli;

import com.hollingsworth.arsnouveau.api.recipe.GlyphPressRecipe;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class GlyphPressProcessor implements IComponentProcessor {

    GlyphPressRecipe recipe;
    @Override
    public void setup(IVariableProvider variables) {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        String recipeID = variables.get("recipe").asString();
        recipe = (GlyphPressRecipe) manager.byKey(new ResourceLocation(recipeID)).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public IVariable process(String s) {
        if(s.equals("clay_type"))
            return IVariable.from(recipe.getClay());
        if(s.equals("reagent"))
            return IVariable.from(recipe.reagent);
        if(s.equals("tier"))
            return IVariable.wrap(new TranslationTextComponent("ars_nouveau.spell_tier." + recipe.tier.toString().toLowerCase()).getString());
        if(s.equals("mana_cost") ){
            if(recipe.output.getItem() instanceof Glyph){
                int cost =  ((Glyph) recipe.output.getItem()).spellPart.getManaCost();
                String costLang = "";
                if(cost == 0)
                    costLang = new TranslationTextComponent("ars_nouveau.mana_cost.none").getString();
                if(cost < 20)
                    costLang = new TranslationTextComponent("ars_nouveau.mana_cost.low").getString();
                if(cost < 50)
                    costLang = new TranslationTextComponent("ars_nouveau.mana_cost.medium").getString();
                if(cost >= 50)
                    costLang = new TranslationTextComponent("ars_nouveau.mana_cost.high").getString();
                return IVariable.wrap(costLang);
            }
            return IVariable.wrap("");
        }
        return null;
    }
}
