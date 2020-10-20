package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.recipe.GlyphPressRecipe;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIArsNouveauPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ArsNouveau.MODID, "main");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(
            new GlyphPressRecipeCategory(registry.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        List<GlyphPressRecipe> recipeList = new ArrayList<>();
        for(AbstractSpellPart spellPart : ArsNouveauAPI.getInstance().getSpell_map().values()){
            recipeList.add(new GlyphPressRecipe(new ResourceLocation(ArsNouveau.MODID,"glyph_" + spellPart.tag), spellPart.getTier(), new ItemStack(spellPart.getCraftingReagent()), ArsNouveauAPI.getInstance().getGlyphItem(spellPart).getDefaultInstance()));
        }
        registry.addRecipes(recipeList, GlyphPressRecipeCategory.UID);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.GLYPH_PRESS_BLOCK), GlyphPressRecipeCategory.UID);
    }
}
