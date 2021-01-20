package com.hollingsworth.arsnouveau.common.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.hollingsworth.arsnouveau.api.recipe.GlyphPressRecipe;
import com.hollingsworth.arsnouveau.api.spell.ISpellTier;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Arrays;

@ZenRegister
@ZenCodeType.Name("mods.arsnouveau.GlyphPress")
public class GlyphPressManager implements IRecipeManager {
    
    @ZenCodeType.Method
    public void addRecipe(String name, String tier, IItemStack reagent, IItemStack output) {
        name = fixRecipeName(name);
        ISpellTier.Tier spellTier = null;
        for(ISpellTier.Tier value : ISpellTier.Tier.values()) {
            if(value.name().equalsIgnoreCase(tier)) {
                spellTier = value;
            }
        }
        if(spellTier == null) {
            throw new IllegalArgumentException("Invalid spell tier! Valid values are: " + Arrays.toString(ISpellTier.Tier.values()));
        }
        GlyphPressRecipe recipe = new GlyphPressRecipe(new ResourceLocation("crafttweaker", name), spellTier, reagent.getInternal(), output.getInternal());
        CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe, ""));
    }
    
    @Override
    public IRecipeType<GlyphPressRecipe> getRecipeType() {
        return RecipeRegistry.GLYPH_TYPE;
    }
}
