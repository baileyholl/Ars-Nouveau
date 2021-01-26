package com.hollingsworth.arsnouveau.api.enchanting_apparatus;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EnchantingApparatusRecipe implements IEnchantingRecipe{

    public Ingredient reagent; // Used in the arcane pedestal
    public ItemStack result; // Result item
    public List<Ingredient> pedestalItems; // Items part of the recipe
    public String category;
    public final ResourceLocation id;
    public final int manaCost;

    public EnchantingApparatusRecipe(ItemStack result, Ingredient reagent, List<Ingredient> pedestalItems, String category){
        this.reagent = reagent;
        this.pedestalItems = pedestalItems;
        this.result = result;
        this.category = category;
        manaCost = 0;
        this.id = new ResourceLocation(ArsNouveau.MODID, result.getItem().getRegistryName().getPath());
    }

    public EnchantingApparatusRecipe(ResourceLocation id,   List<Ingredient> pedestalItems, Ingredient reagent,ItemStack result){
        this.reagent = reagent;
        this.pedestalItems = pedestalItems;
        this.result = result;
        this.category = "";
        manaCost = 0;
        this.id = id;
    }

    public EnchantingApparatusRecipe(){
        reagent = Ingredient.EMPTY;
        result = ItemStack.EMPTY;
        pedestalItems = new ArrayList<>();
        manaCost = 0;
        this.id = new ResourceLocation(ArsNouveau.MODID, "empty");
    }


    public EnchantingApparatusRecipe(Item result, Item reagent, Item[] pedestalItems, String category){
        ArrayList<Ingredient> stacks = new ArrayList<>();
        for(Item i : pedestalItems){
            stacks.add(Ingredient.fromItems(i));
        }
        this.reagent = Ingredient.fromItems(reagent);
        this.result = new ItemStack(result);
        this.pedestalItems = stacks;
        this.category = category;
        manaCost = 0;
        this.id = new ResourceLocation(ArsNouveau.MODID, result.getRegistryName().getPath());
    }

    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        pedestalItems = pedestalItems.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
        if (!this.reagent.test(reagent)|| this.pedestalItems.size() != pedestalItems.size() || !doItemsMatch(pedestalItems, this.pedestalItems)) {
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
        return Objects.equals(reagent, that.reagent) &&
                Objects.equals(pedestalItems, that.pedestalItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reagent, pedestalItems);
    }

    @Override
    public String toString() {
        return "EnchantingApparatusRecipe{" +
                "catalyst=" + reagent +
                ", result=" + result +
                ", pedestalItems=" + pedestalItems +
                '}';
    }

    public JsonElement asRecipe(){
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "ars_nouveau:enchanting_apparatus");
        int counter = 1;
        for(Ingredient i : pedestalItems){
            JsonArray item = new JsonArray();
            item.add(i.serialize());
            jsonobject.add("item_"+counter, item);
            counter++;
        }
        JsonArray reagent =  new JsonArray();
        reagent.add(this.reagent.serialize());
        jsonobject.add("reagent", reagent);

        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("item", this.result.getItem().getRegistryName().toString());
        int count = this.result.getCount();
        if (count > 1) {
            resultObj.addProperty("count", count);
        }

        jsonobject.add("output", resultObj);
        return jsonobject;
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
        infoPage.addProperty("recipe", this.result.getItem().getRegistryName().toString());


        jsonArray.add(descPage);
        jsonArray.add(infoPage);
        jsonobject.add("pages", jsonArray);
        return jsonobject;
    }

    @Override
    public boolean consumesMana() {
        return manaCost() > 0;
    }

    @Override
    public int manaCost() {
        return manaCost;
    }

    @Override
    public boolean matches(EnchantingApparatusTile tile, World worldIn) {
        return isMatch(tile.getPedestalItems(), tile.catalystItem, tile);
    }

    @Override
    public ItemStack getCraftingResult(EnchantingApparatusTile inv) {
        return this.result;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeRegistry.APPARATUS_SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return Registry.RECIPE_TYPE.getOrDefault(new ResourceLocation(ArsNouveau.MODID, "enchanting_apparatus"));
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<EnchantingApparatusRecipe> {

        @Override
        public EnchantingApparatusRecipe read(ResourceLocation recipeId, JsonObject json) {
            Ingredient reagent = Ingredient.deserialize(JSONUtils.getJsonArray(json, "reagent"));
            ItemStack output = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "output"));
            List<Ingredient> stacks = new ArrayList<>();
            for(int i = 1; i < 9; i++){
                if(json.has("item_"+i))
                    stacks.add(Ingredient.deserialize(JSONUtils.getJsonArray(json, "item_" + i)));
            }
            return new EnchantingApparatusRecipe(recipeId, stacks, reagent, output);
        }

        @Nullable
        @Override
        public EnchantingApparatusRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            int length = buffer.readInt();
            Ingredient reagent = Ingredient.read(buffer);
            ItemStack output = buffer.readItemStack();
            List<Ingredient> stacks = new ArrayList<>();

            for(int i = 0; i < length; i++){
                try{ stacks.add(Ingredient.read(buffer)); }catch (Exception e){
                    e.printStackTrace();
                    break;
                }
            }
            return new EnchantingApparatusRecipe(recipeId, stacks, reagent, output);
        }

        @Override
        public void write(PacketBuffer buf, EnchantingApparatusRecipe recipe) {
            buf.writeInt(recipe.pedestalItems.size());
            recipe.reagent.write(buf);
            buf.writeItemStack(recipe.result);
            for(Ingredient i : recipe.pedestalItems){
                i.write(buf);
            }
        }
    }
}
