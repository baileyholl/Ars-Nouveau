package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantmentRecipe;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class ApparatusRecipeBuilder {

    private Ingredient reagent;
    private ItemStack result;
    private List<Ingredient> pedestalItems = new ArrayList<>();
    private int sourceCost;
    private boolean keepNbtOfReagent;
    private ResourceLocation id;

    public ApparatusRecipeBuilder() {}

    public static ApparatusRecipeBuilder builder() {
        return new ApparatusRecipeBuilder();
    }

    public ApparatusRecipeBuilder withResult(ItemLike result) {
        this.result = new ItemStack(result);
        return this;
    }

    public ApparatusRecipeBuilder withResult(ItemLike result, int count) {
        this.result = new ItemStack(result, count);
        return this;
    }

    public ApparatusRecipeBuilder withResult(ItemStack result) {
        this.result = result;
        return this;
    }

    public ApparatusRecipeBuilder withReagent(ItemLike provider) {
        this.reagent = Ingredient.of(provider);
        return this;
    }


    public ApparatusRecipeBuilder withReagent(Ingredient ingredient) {
        this.reagent = ingredient;
        return this;
    }

    public ApparatusRecipeBuilder withPedestalItem(Ingredient i) {
        this.pedestalItems.add(i);
        return this;
    }


    public ApparatusRecipeBuilder withPedestalItem(ItemLike i) {
        return this.withPedestalItem(Ingredient.of(i));
    }


    public ApparatusRecipeBuilder withPedestalItem(int count, ItemLike item) {
        for (int i = 0; i < count; i++)
            this.withPedestalItem(item);
        return this;
    }

    public ApparatusRecipeBuilder withPedestalItem(int count, Ingredient ingred) {
        for (int i = 0; i < count; i++)
            this.withPedestalItem(ingred);
        return this;
    }


    public ApparatusRecipeBuilder withPedestalItem(int count, TagKey<Item> ingred) {
        return this.withPedestalItem(count, Ingredient.of(ingred));
    }

    public ApparatusRecipeBuilder keepNbtOfReagent(boolean keepEnchantmentsOfReagent) {
        this.keepNbtOfReagent = keepEnchantmentsOfReagent;
        return this;
    }

    public ApparatusRecipeBuilder withSourceCost(int cost) {
        this.sourceCost = cost;
        return this;
    }

    public ApparatusRecipeBuilder withId(ResourceLocation id) {
        this.id = id;
        return this;
    }

    public RecipeWrapper build() {
        if (id == null || id.getPath().equals("empty"))
            id = ArsNouveau.prefix( getRegistryName(result.getItem()).getPath());
        if(result.isEmpty()){
            throw new IllegalStateException("Enchanting Apparatus Recipe has no result");
        }

        return new RecipeWrapper(id, new EnchantingApparatusRecipe(reagent, result, pedestalItems, sourceCost, keepNbtOfReagent), EnchantingApparatusRecipe.Serializer.CODEC);
    }

    public RecipeWrapper buildEnchantmentRecipe(ResourceKey<Enchantment> enchantment, int level, int mana) {
        if(id == null || id.getPath().equals("empty")){
            id = ArsNouveau.prefix(enchantment.location().getPath() + "_" + level);
        }
        return new RecipeWrapper(id, new EnchantmentRecipe(this.pedestalItems, enchantment, level, mana), EnchantmentRecipe.Serializer.CODEC);
    }

    public record RecipeWrapper<T extends EnchantingApparatusRecipe>(ResourceLocation id, T recipe, MapCodec<T> codec) {

    }
}
