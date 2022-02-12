package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantmentRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;

public class ApparatusRecipeBuilder {
    EnchantingApparatusRecipe recipe;
    public ApparatusRecipeBuilder(){
        this.recipe = new EnchantingApparatusRecipe();
    }

    public static ApparatusRecipeBuilder builder(){
        return new ApparatusRecipeBuilder();
    }
    public ApparatusRecipeBuilder withResult(ItemLike result){
        this.recipe.result = new ItemStack(result);
        return this;
    }
    public ApparatusRecipeBuilder withResult(ItemStack result){
        this.recipe.result = result;
        return this;
    }

    public ApparatusRecipeBuilder withReagent(ItemLike provider){
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

    public ApparatusRecipeBuilder withPedestalItem(ItemLike i){
        return this.withPedestalItem(Ingredient.of(i));
    }

    public ApparatusRecipeBuilder withPedestalItem(int count, ItemLike item){
        for(int i = 0; i < count; i++)
            this.withPedestalItem(item);
        return this;
    }

    public ApparatusRecipeBuilder withPedestalItem(int count, Ingredient ingred){
        for(int i = 0; i < count; i++)
            this.withPedestalItem(ingred);
        return this;
    }

    public ApparatusRecipeBuilder keepEnchantmentsOfReagent(boolean keepEnchantmentsOfReagent) {
        this.recipe.keepEnchantmentsOfReagent = keepEnchantmentsOfReagent;
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
