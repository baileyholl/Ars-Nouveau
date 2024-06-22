package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;


public class ImbuementRecipe implements Recipe<ImbuementTile> {
    public final Ingredient input;
    public final ItemStack output;
    public final int source;
    public final ResourceLocation id;

    public List<Ingredient> pedestalItems;

    public ImbuementRecipe(ResourceLocation resourceLocation, Ingredient input, ItemStack output, int source, List<Ingredient> pedestalItems) {
        this.id = resourceLocation;
        this.input = input;
        this.output = output;
        this.source = source;
        this.pedestalItems = pedestalItems;
    }

    public ImbuementRecipe(String id, Ingredient ingredient, ItemStack output, int source, List<Ingredient> pedestalItems) {
        this(ArsNouveau.prefix( RecipeRegistry.IMBUEMENT_RECIPE_ID + "_" + id), ingredient, output, source, pedestalItems);
    }

    public ImbuementRecipe(String id, Ingredient ingredient, ItemStack output, int source) {
        this(ArsNouveau.prefix( RecipeRegistry.IMBUEMENT_RECIPE_ID + "_" + id), ingredient, output, source, new ArrayList<>());
    }

    public ImbuementRecipe withPedestalItem(Ingredient i) {
        this.pedestalItems.add(i);
        return this;
    }

    public ImbuementRecipe withPedestalItem(RegistryObject<? extends ItemLike> i) {
        return withPedestalItem(i.get());
    }

    public ImbuementRecipe withPedestalItem(ItemStack i) {
        this.pedestalItems.add(Ingredient.of(i));
        return this;
    }

    public ImbuementRecipe withPedestalItem(ItemLike i) {
        this.pedestalItems.add(Ingredient.of(i));
        return this;
    }

    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, ImbuementTile imbuementTile, @Nullable Player player) {
        pedestalItems = pedestalItems.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
        return doesReagentMatch(reagent) && this.pedestalItems.size() == pedestalItems.size() && EnchantingApparatusRecipe.doItemsMatch(pedestalItems, this.pedestalItems);
    }

    public boolean doesReagentMatch(ItemStack reag) {
        return this.input.test(reag);
    }

    @Override
    public boolean matches(ImbuementTile pContainer, Level pLevel) {
        return this.input.test(pContainer.getItem(0)) && EnchantingApparatusRecipe.doItemsMatch(pContainer.getPedestalItems(), pedestalItems);
    }

    @Override
    public ItemStack assemble(ImbuementTile pCraftingContainer, HolderLookup.Provider pRegistries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.IMBUEMENT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return BuiltInRegistries.RECIPE_TYPE.get(ArsNouveau.prefix( RecipeRegistry.IMBUEMENT_RECIPE_ID));
    }

    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "ars_nouveau:" + RecipeRegistry.IMBUEMENT_RECIPE_ID);
        jsonobject.add("input", input.toJson());
        jsonobject.addProperty("output", getRegistryName(output.getItem()).toString());
        jsonobject.addProperty("count", output.getCount());
        jsonobject.addProperty("source", source);
        JsonArray pedestalArr = new JsonArray();
        for (Ingredient i : this.pedestalItems) {
            JsonObject object = new JsonObject();
            object.add("item", i.toJson());
            pedestalArr.add(object);
        }
        jsonobject.add("pedestalItems", pedestalArr);
        return jsonobject;
    }

    public static class Serializer implements RecipeSerializer<ImbuementRecipe> {

        @Override
        public ImbuementRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient inputStack = null;
            if (GsonHelper.isArrayNode(json, "input")) {
                inputStack = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "input"));
            } else {
                inputStack = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            }
            Item output = GsonHelper.getAsItem(json, "output");
            int count = GsonHelper.getAsInt(json, "count");
            ItemStack outputStack = new ItemStack(output, count);
            int source = GsonHelper.getAsInt(json, "source");
            JsonArray pedestalItems = GsonHelper.getAsJsonArray(json, "pedestalItems");
            List<Ingredient> stacks = new ArrayList<>();

            for (JsonElement e : pedestalItems) {
                JsonObject obj = e.getAsJsonObject();
                Ingredient input = null;
                if (GsonHelper.isArrayNode(obj, "item")) {
                    input = Ingredient.fromJson(GsonHelper.getAsJsonArray(obj, "item"));
                } else {
                    input = Ingredient.fromJson(GsonHelper.getAsJsonObject(obj, "item"));
                }
                stacks.add(input);
            }
            return new ImbuementRecipe(recipeId, inputStack, outputStack, source, stacks);
        }


        @Override
        public void toNetwork(FriendlyByteBuf buf, ImbuementRecipe recipe) {
            buf.writeInt(recipe.pedestalItems.size());
            for (Ingredient i : recipe.pedestalItems) {
                i.toNetwork(buf);
            }
            recipe.input.toNetwork(buf);
            buf.writeItem(recipe.output);
            buf.writeInt(recipe.source);
        }

        @Nullable
        @Override
        public ImbuementRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int length = buffer.readInt();
            List<Ingredient> stacks = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                try {
                    stacks.add(Ingredient.fromNetwork(buffer));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            return new ImbuementRecipe(recipeId, Ingredient.fromNetwork(buffer), buffer.readItem(), buffer.readInt(), stacks);
        }
    }
}
