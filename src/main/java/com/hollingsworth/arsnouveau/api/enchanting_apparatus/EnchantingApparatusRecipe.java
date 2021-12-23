package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
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
    public ResourceLocation id;
    public int manaCost;

    public static final String RECIPE_ID = "enchanting_apparatus";

    public EnchantingApparatusRecipe(ItemStack result, Ingredient reagent, List<Ingredient> pedestalItems){
        this.reagent = reagent;
        this.pedestalItems = pedestalItems;
        this.result = result;
        manaCost = 0;
        this.id = new ResourceLocation(ArsNouveau.MODID, result.getItem().getRegistryName().getPath());
    }

    public EnchantingApparatusRecipe(ResourceLocation id,   List<Ingredient> pedestalItems, Ingredient reagent,ItemStack result){
        this(id, pedestalItems, reagent, result, 0);
    }

    public EnchantingApparatusRecipe(ResourceLocation id,   List<Ingredient> pedestalItems, Ingredient reagent,ItemStack result, int cost){
        this.reagent = reagent;
        this.pedestalItems = pedestalItems;
        this.result = result;
        manaCost = cost;
        this.id = id;
    }
    public EnchantingApparatusRecipe(){
        reagent = Ingredient.EMPTY;
        result = ItemStack.EMPTY;
        pedestalItems = new ArrayList<>();
        manaCost = 0;
        this.id = new ResourceLocation(ArsNouveau.MODID, "empty");
    }

    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile, @Nullable Player player) {
        pedestalItems = pedestalItems.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
        return doesReagentMatch(reagent) && this.pedestalItems.size() == pedestalItems.size() && doItemsMatch(pedestalItems, this.pedestalItems);
    }

    public boolean doesReagentMatch(ItemStack reag){
        return this.reagent.test(reag);
    }

    @Override
    public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        return result.copy();
    }


    // Function to check if both arrays are same
    static boolean doItemsMatch(List<ItemStack> inputs, List<Ingredient> recipeItems) {
        StackedContents recipeitemhelper = new StackedContents();
        for(ItemStack i : inputs)
            recipeitemhelper.accountStack(i, 1);

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

        JsonArray pedestalArr = new JsonArray();
        for(Ingredient i : this.pedestalItems){
            JsonObject object = new JsonObject();
            object.add("item", i.toJson());
            pedestalArr.add(object);
        }
        JsonArray reagent =  new JsonArray();
        reagent.add(this.reagent.toJson());
        jsonobject.add("reagent", reagent);

        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("item", this.result.getItem().getRegistryName().toString());
        int count = this.result.getCount();
        if (count > 1) {
            resultObj.addProperty("count", count);
        }
        jsonobject.add("pedestalItems", pedestalArr);
        jsonobject.add("output", resultObj);
        jsonobject.addProperty("sourceCost", manaCost);
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
    public boolean matches(EnchantingApparatusTile tile, Level worldIn) {
        return isMatch(tile.getPedestalItems(), tile.catalystItem, tile, null);
    }

    public boolean matches(EnchantingApparatusTile tile, Level worldIn, @Nullable Player playerEntity) {
        return isMatch(tile.getPedestalItems(), tile.catalystItem, tile, playerEntity);
    }

    @Override
    public ItemStack assemble(EnchantingApparatusTile inv) {
        return this.result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.APPARATUS_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return Registry.RECIPE_TYPE.get(new ResourceLocation(ArsNouveau.MODID, RECIPE_ID));
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<EnchantingApparatusRecipe> {

        @Override
        public EnchantingApparatusRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient reagent = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "reagent"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            int cost = json.has("sourceCost") ? GsonHelper.getAsInt(json, "sourceCost") : 0;
            JsonArray pedestalItems = GsonHelper.getAsJsonArray(json,"pedestalItems");
            List<Ingredient> stacks = new ArrayList<>();

            for(JsonElement e : pedestalItems){
                JsonObject obj = e.getAsJsonObject();
                Ingredient input = null;
                if(GsonHelper.isArrayNode(obj, "item")){
                    input = Ingredient.fromJson(GsonHelper.getAsJsonArray(obj, "item"));
                }else{
                    input = Ingredient.fromJson(GsonHelper.getAsJsonObject(obj, "item"));
                }
                stacks.add(input);
            }
            return new EnchantingApparatusRecipe(recipeId, stacks, reagent, output, cost);
        }

        @Nullable
        @Override
        public EnchantingApparatusRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int length = buffer.readInt();
            Ingredient reagent = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();
            List<Ingredient> stacks = new ArrayList<>();

            for(int i = 0; i < length; i++){
                try{ stacks.add(Ingredient.fromNetwork(buffer)); }catch (Exception e){
                    e.printStackTrace();
                    break;
                }
            }
            int cost = buffer.readInt();
            return new EnchantingApparatusRecipe(recipeId, stacks, reagent, output, cost);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, EnchantingApparatusRecipe recipe) {
            buf.writeInt(recipe.pedestalItems.size());
            recipe.reagent.toNetwork(buf);
            buf.writeItem(recipe.result);
            for(Ingredient i : recipe.pedestalItems){
                i.toNetwork(buf);
            }
            buf.writeInt(recipe.manaCost);
        }
    }
}
