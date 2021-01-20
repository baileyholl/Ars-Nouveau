package com.hollingsworth.arsnouveau.common.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.recipe.GlyphPressRecipe;
import com.hollingsworth.arsnouveau.api.spell.ISpellTier;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Arrays;
import java.util.stream.Collectors;

@ZenRegister
@ZenCodeType.Name("mods.arsnouveau.EnchantingApparatus")
public class EnchantingApparatusManager implements IRecipeManager {
    
    @ZenCodeType.Method
    public void addRecipe(String name, IItemStack result, IIngredient reagent, IIngredient[] pedestalItems) {
        name = fixRecipeName(name);
        EnchantingApparatusRecipe recipe = new EnchantingApparatusRecipe(new ResourceLocation("crafttweaker", name), Arrays.stream(pedestalItems).map(IIngredient::asVanillaIngredient).collect(Collectors.toList()), reagent.asVanillaIngredient(), result.getInternal());
        CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe, ""));
    }
    
    @Override
    public IRecipeType<EnchantingApparatusRecipe> getRecipeType() {
        return RecipeRegistry.APPARATUS_TYPE;
    }
}
