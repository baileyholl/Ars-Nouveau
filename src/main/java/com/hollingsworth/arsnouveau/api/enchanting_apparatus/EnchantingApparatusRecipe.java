package com.hollingsworth.arsnouveau.api.enchanting_apparatus;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EnchantingApparatusRecipe implements IEnchantingRecipe{

    public Ingredient catalyst; // Used in the arcane pedestal
    public ItemStack result; // Result item
    public List<Ingredient> pedestalItems; // Items part of the recipe
    public String description;
    private String category;

    public EnchantingApparatusRecipe(ItemStack result, Ingredient catalyst, List<Ingredient> pedestalItems, String category){
        this.catalyst = catalyst;
        this.pedestalItems = pedestalItems;
        this.result = result;
        this.category = category;
    }


    public EnchantingApparatusRecipe(Item result, Item catalyst, Item[] pedestalItems, String category){
        ArrayList<Ingredient> stacks = new ArrayList<>();
        for(Item i : pedestalItems){
            stacks.add(Ingredient.fromItems(i));
        }
        this.catalyst = Ingredient.fromItems(catalyst);
        this.result = new ItemStack(result);
        this.pedestalItems = stacks;
        this.category = category;
    }

    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        pedestalItems = pedestalItems.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
        if (!catalyst.test(reagent)|| this.pedestalItems.size() != pedestalItems.size() || !doItemsMatch(pedestalItems, this.pedestalItems)) {
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        return result;
    }


    // Function to check if both arrays are same
    static boolean doItemsMatch(List<ItemStack> inputs, List<Ingredient> recipeItems) {
        RecipeItemHelper recipeitemhelper = new RecipeItemHelper();
        for(ItemStack i : inputs)
            recipeitemhelper.func_221264_a(i, 1);


        return inputs.size() == recipeItems.size() && (net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs,  recipeItems) != null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnchantingApparatusRecipe that = (EnchantingApparatusRecipe) o;
        return Objects.equals(catalyst, that.catalyst) &&
                Objects.equals(pedestalItems, that.pedestalItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(catalyst, pedestalItems);
    }

    @Override
    public String toString() {
        return "EnchantingApparatusRecipe{" +
                "catalyst=" + catalyst +
                ", result=" + result +
                ", pedestalItems=" + pedestalItems +
                '}';
    }

    /**
     * Converts to a patchouli documentation page
     */
    public JsonElement serialize() {
        JsonObject jsonobject = new JsonObject();

        jsonobject.addProperty("name", this.result.getItem().getTranslationKey());
        jsonobject.addProperty("icon",  this.result.getItem().getRegistryName().toString());
        jsonobject.addProperty("category", this.category);
        JsonArray jsonArray = new JsonArray();
        JsonObject descPage = new JsonObject();
        descPage.addProperty("type", "text");
        descPage.addProperty("text",ArsNouveau.MODID + ".page." + this.result.getItem().getRegistryName().toString().replace(ArsNouveau.MODID + ":", ""));
        JsonObject infoPage = new JsonObject();
        infoPage.addProperty("type", "apparatus_recipe");
        infoPage.addProperty("reagent", this.catalyst.getMatchingStacks()[0].getItem().getRegistryName().toString());


        if(this.pedestalItems != null){
            AtomicInteger count = new AtomicInteger(1);
            this.pedestalItems.forEach(i ->{
                infoPage.addProperty("item" + count.get(), i.getMatchingStacks()[0].getItem().getRegistryName().toString());
                count.addAndGet(1);
            });

        }


        jsonArray.add(descPage);
        jsonArray.add(infoPage);
        jsonobject.add("pages", jsonArray);
        return jsonobject;
    }

    @Override
    public boolean consumesMana() {
        return false;
    }

    @Override
    public int manaCost() {
        return 0;
    }
}
