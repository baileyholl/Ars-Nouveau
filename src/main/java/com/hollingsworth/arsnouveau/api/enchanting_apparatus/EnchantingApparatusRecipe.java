package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.util.Log;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.RegistryAccess;
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
import net.neoforged.neoforge.registries.ForgeRegistries;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class EnchantingApparatusRecipe implements IEnchantingRecipe {

    public Ingredient reagent; // Used in the arcane pedestal
    public ItemStack result; // Result item
    public List<Ingredient> pedestalItems; // Items part of the recipe
    public ResourceLocation id;
    public int sourceCost;
    public boolean keepNbtOfReagent = false;


    public EnchantingApparatusRecipe(ItemStack result, Ingredient reagent, List<Ingredient> pedestalItems) {
        this.reagent = reagent;
        this.pedestalItems = pedestalItems;
        this.result = result;
        sourceCost = 0;
        this.id = new ResourceLocation(ArsNouveau.MODID, getRegistryName(result.getItem()).getPath());
    }

    public EnchantingApparatusRecipe(ResourceLocation id, List<Ingredient> pedestalItems, Ingredient reagent, ItemStack result) {
        this(id, pedestalItems, reagent, result, 0, false);
    }

    public EnchantingApparatusRecipe(ResourceLocation id, List<Ingredient> pedestalItems, Ingredient reagent, ItemStack result, int cost, boolean keepNbtOfReagent) {
        this.reagent = reagent;
        this.pedestalItems = pedestalItems;
        this.result = result;
        sourceCost = cost;
        this.id = id;
        this.keepNbtOfReagent = keepNbtOfReagent;
    }

    public EnchantingApparatusRecipe() {
        reagent = Ingredient.EMPTY;
        result = ItemStack.EMPTY;
        pedestalItems = new ArrayList<>();
        sourceCost = 0;
        this.id = new ResourceLocation(ArsNouveau.MODID, "empty");
    }

    public boolean excludeJei(){
        return false;
    }

    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile, @Nullable Player player) {
        pedestalItems = pedestalItems.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
        return doesReagentMatch(reagent) && this.pedestalItems.size() == pedestalItems.size() && doItemsMatch(pedestalItems, this.pedestalItems);
    }

    public boolean doesReagentMatch(ItemStack reag) {
        return this.reagent.test(reag);
    }

    @Override
    public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        ItemStack result = this.result.copy();
        if (keepNbtOfReagent && reagent.hasTag()) {
            result.setTag(reagent.getTag());
            result.setDamageValue(0);
        }
        return result.copy();
    }


    // Function to check if both arrays are same
    public static boolean doItemsMatch(List<ItemStack> inputs, List<Ingredient> recipeItems) {
        StackedContents recipeitemhelper = new StackedContents();
        for (ItemStack i : inputs)
            recipeitemhelper.accountStack(i, 1);

        return inputs.size() == recipeItems.size() && (net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(inputs, recipeItems) != null);
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
    // TODO: 1.20 unnest item and use tag/item directly
    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "ars_nouveau:enchanting_apparatus");

        JsonArray pedestalArr = new JsonArray();
        for (Ingredient i : this.pedestalItems) {
            pedestalArr.add(i.toJson());
        }
        JsonArray reagent = new JsonArray();
        reagent.add(this.reagent.toJson());
        jsonobject.add("reagent", reagent);

        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("item", getRegistryName(result.getItem()).toString());
        int count = this.result.getCount();
        if (count > 1) {
            resultObj.addProperty("count", count);
        }
        jsonobject.add("pedestalItems", pedestalArr);
        jsonobject.add("output", resultObj);
        jsonobject.addProperty("sourceCost", sourceCost);
        jsonobject.addProperty("keepNbtOfReagent", keepNbtOfReagent);
        return jsonobject;
    }

    @Override
    public boolean consumesSource() {
        return getSourceCost() > 0;
    }

    @Override
    public int getSourceCost() {
        return sourceCost;
    }

    @Override
    public boolean matches(EnchantingApparatusTile tile, Level worldIn) {
        return isMatch(tile.getPedestalItems(), tile.getStack(), tile, null);
    }

    public boolean matches(EnchantingApparatusTile tile, Level worldIn, @Nullable Player playerEntity) {
        return isMatch(tile.getPedestalItems(), tile.getStack(), tile, playerEntity);
    }

    @Override
    public ItemStack assemble(EnchantingApparatusTile p_44001_, RegistryAccess p_267165_) {
        return this.result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return ItemStack.EMPTY;
    }


    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.APPARATUS_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ForgeRegistries.RECIPE_TYPES.getValue(new ResourceLocation(ArsNouveau.MODID, RecipeRegistry.ENCHANTING_APPARATUS_RECIPE_ID));
    }


    public static class Serializer implements RecipeSerializer<EnchantingApparatusRecipe> {

        private List<Ingredient> getPedestalItems(ResourceLocation recipeId, JsonArray pedestalJson) {
            return StreamSupport.stream(pedestalJson.spliterator(), true).map(el -> {
                if (el instanceof JsonObject obj && (GsonHelper.isObjectNode(obj, "item") || GsonHelper.isArrayNode(obj, "item"))) {
                    Log.getLogger().warn("Use of deprecated recipe format in recipe ID: {}", recipeId);
                    return Ingredient.fromJson(obj.get("item"));
                }
                return Ingredient.fromJson(el);
            }).collect(Collectors.toList());
        }

        @Override
        public EnchantingApparatusRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient reagent = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "reagent"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            int cost = json.has("sourceCost") ? GsonHelper.getAsInt(json, "sourceCost") : 0;
            boolean keepNbtOfReagent = json.has("keepNbtOfReagent") && GsonHelper.getAsBoolean(json, "keepNbtOfReagent");
            JsonArray pedestalItems = GsonHelper.getAsJsonArray(json, "pedestalItems");
            List<Ingredient> stacks = getPedestalItems(recipeId, pedestalItems);
            return new EnchantingApparatusRecipe(recipeId, stacks, reagent, output, cost, keepNbtOfReagent);
        }

        @Nullable
        @Override
        public EnchantingApparatusRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int length = buffer.readInt();
            Ingredient reagent = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();
            List<Ingredient> stacks = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                try {
                    stacks.add(Ingredient.fromNetwork(buffer));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            int cost = buffer.readInt();
            boolean keepNbtOfReagent = buffer.readBoolean();
            return new EnchantingApparatusRecipe(recipeId, stacks, reagent, output, cost, keepNbtOfReagent);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, EnchantingApparatusRecipe recipe) {
            buf.writeInt(recipe.pedestalItems.size());
            recipe.reagent.toNetwork(buf);
            buf.writeItem(recipe.result);
            for (Ingredient i : recipe.pedestalItems) {
                i.toNetwork(buf);
            }
            buf.writeInt(recipe.sourceCost);
            buf.writeBoolean(recipe.keepNbtOfReagent);
        }
    }
}
