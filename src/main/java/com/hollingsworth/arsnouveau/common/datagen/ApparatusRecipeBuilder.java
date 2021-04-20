package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantmentRecipe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class ApparatusRecipeBuilder {
    EnchantingApparatusRecipe recipe;
    public ApparatusRecipeBuilder(){
        this.recipe = new EnchantingApparatusRecipe();
    }

    public static ApparatusRecipeBuilder builder(){
        return new ApparatusRecipeBuilder();
    }
    public ApparatusRecipeBuilder withResult(IItemProvider result){
        this.recipe.result = new ItemStack(result);
        return this;
    }
    public ApparatusRecipeBuilder withResult(ItemStack result){
        this.recipe.result = result;
        return this;
    }

    public ApparatusRecipeBuilder withCategory(ArsNouveauAPI.PatchouliCategories category){
        this.recipe.category = category.name();
        return this;
    }
    public ApparatusRecipeBuilder withReagent(IItemProvider provider){
        this.recipe.reagent = Ingredient.of(provider);
        return this;
    }

    public ApparatusRecipeBuilder withReagent(Ingredient ingredient){
        this.recipe.reagent = ingredient;
        return this;
    }

    public ApparatusRecipeBuilder withPedestalItem(Ingredient i){
        this.recipe.pedestalItems.add(i);
        return this;
    }

    public ApparatusRecipeBuilder withPedestalItem(IItemProvider i){
        return this.withPedestalItem(Ingredient.of(i));
    }

    public ApparatusRecipeBuilder withPedestalItem(int count, IItemProvider item){
        for(int i = 0; i < count; i++)
            this.withPedestalItem(item);
        return this;
    }

    public ApparatusRecipeBuilder withPedestalItem(int count, Ingredient ingred){
        for(int i = 0; i < count; i++)
            this.withPedestalItem(ingred);
        return this;
    }

    public EnchantingApparatusRecipe build(){
        if(recipe.id.getPath().equals("empty"))
            recipe.id = new ResourceLocation(ArsNouveau.MODID, recipe.result.getItem().getRegistryName().getPath());
        return recipe;
    }

    public EnchantmentRecipe buildEnchantmentRecipe(Enchantment enchantment, int level, int mana){
        return new EnchantmentRecipe(this.recipe.pedestalItems, enchantment, level, mana);
    }

}
