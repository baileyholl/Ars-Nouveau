package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class CrushRecipe implements Recipe<Container> {

    public final Ingredient input;
    public final List<CrushOutput> outputs;
    public final ResourceLocation id;
    private boolean skipBlockPlace;

    public CrushRecipe(ResourceLocation id, Ingredient input, List<CrushOutput> outputs, boolean skipBlockPlace) {
        this.input = input;
        this.outputs = outputs;
        this.id = id;
        this.skipBlockPlace = skipBlockPlace;
    }

    @Deprecated
    public CrushRecipe(ResourceLocation id, Ingredient input, List<CrushOutput> outputs) {
        this(id, input, outputs, false);
    }

    public CrushRecipe(String id, Ingredient input, List<CrushOutput> outputs) {
        this(new ResourceLocation(ArsNouveau.MODID, "crush_" + id), input, outputs, false);
    }

    public CrushRecipe(String id, Ingredient input, List<CrushOutput> outputs, boolean skipBlockPlace) {
        this(new ResourceLocation(ArsNouveau.MODID, "crush_" + id), input, outputs, skipBlockPlace);
    }

    public CrushRecipe(String id, Ingredient input) {
        this(id, input, new ArrayList<>());
    }

    public List<ItemStack> getRolledOutputs(RandomSource random) {
        List<ItemStack> finalOutputs = new ArrayList<>();
        for (CrushOutput crushRoll : outputs) {
            if (random.nextDouble() <= crushRoll.chance) {
                if(crushRoll.maxRange > 1){
                    // get a number between 1 and max
                    int num = random.nextInt(crushRoll.maxRange) + 1;
                    for(int i = 0; i < num; i++){
                        finalOutputs.add(crushRoll.stack.copy());
                    }
                }else {
                    finalOutputs.add(crushRoll.stack.copy());
                }
            }
        }

        return finalOutputs;
    }

    public CrushRecipe withItems(ItemStack output, float chance) {
        this.outputs.add(new CrushOutput(output, chance));
        return this;
    }

    public CrushRecipe withItems(ItemStack output) {
        this.outputs.add(new CrushOutput(output, 1.0f));
        return this;
    }

    public CrushRecipe skipBlockPlace() {
        this.skipBlockPlace = true;
        return this;
    }

    public Boolean shouldSkipBlockPlace() {
        return this.skipBlockPlace;
    }

    @Override
    public boolean matches(Container inventory, Level world) {
        return this.input.test(inventory.getItem(0));
    }

    public boolean matches(ItemStack i, Level world) {
        return this.input.test(i);
    }

    @Override
    public ItemStack assemble(Container p_44001_, RegistryAccess p_267165_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.CRUSH_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.CRUSH_TYPE.get();
    }

    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "ars_nouveau:crush");
        jsonobject.add("input", input.toJson());
        JsonArray array = new JsonArray();
        for (CrushOutput output : outputs) {
            JsonObject element = new JsonObject();
            element.addProperty("item", getRegistryName(output.stack.getItem()).toString());
            element.addProperty("chance", output.chance);
            element.addProperty("count", output.stack.getCount());
            element.addProperty("maxRange", output.maxRange);
            array.add(element);
        }
        jsonobject.add("output", array);
        jsonobject.addProperty("skip_block_place", skipBlockPlace);
        return jsonobject;
    }

    public static class CrushOutput {
        public ItemStack stack;
        public float chance;
        public int maxRange; // Can drop the stack multiple times if this is greater than 1

        public CrushOutput(ItemStack stack, float chance) {
            this(stack, chance, 1);
        }

        public CrushOutput(ItemStack stack, float chance, int maxRange) {
            this.stack = stack;
            this.chance = chance;
            this.maxRange = maxRange;
        }
    }

    public static class Serializer implements RecipeSerializer<CrushRecipe> {

        @Override
        public CrushRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient input;
            if (GsonHelper.isArrayNode(json, "input")) {
                input = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "input"));
            } else {
                input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            }
            JsonArray outputs = GsonHelper.getAsJsonArray(json, "output");
            List<CrushOutput> parsedOutputs = new ArrayList<>();

            for (JsonElement e : outputs) {
                JsonObject obj = e.getAsJsonObject();
                float chance = GsonHelper.getAsFloat(obj, "chance");
                String itemId = GsonHelper.getAsString(obj, "item");
                int count = obj.has("count") ? GsonHelper.getAsInt(obj, "count") : 1;
                ItemStack output = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId)), count);
                int maxRange = obj.has("maxRange") ? GsonHelper.getAsInt(obj, "maxRange") : 1;
                parsedOutputs.add(new CrushOutput(output, chance, maxRange));
            }
            boolean skipBlockPlace = json.has("skip_block_place") && GsonHelper.getAsBoolean(json, "skip_block_place");

            return new CrushRecipe(recipeId, input, parsedOutputs, skipBlockPlace);
        }


        @Override
        public void toNetwork(FriendlyByteBuf buf, CrushRecipe recipe) {
            buf.writeInt(recipe.outputs.size());
            recipe.input.toNetwork(buf);
            for (CrushOutput i : recipe.outputs) {
                buf.writeFloat(i.chance);
                buf.writeItemStack(i.stack, false);
                buf.writeInt(i.maxRange);
            }
            buf.writeBoolean(recipe.skipBlockPlace);
        }

        @Nullable
        @Override
        public CrushRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int length = buffer.readInt();
            Ingredient input = Ingredient.fromNetwork(buffer);
            List<CrushOutput> stacks = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                try {
                    float chance = buffer.readFloat();
                    ItemStack outStack = buffer.readItem();
                    int maxRange = buffer.readInt();
                    stacks.add(new CrushOutput(outStack, chance, maxRange));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            boolean skipBlockPlace = buffer.readBoolean();
            return new CrushRecipe(recipeId, input, stacks, skipBlockPlace);
        }
    }
}
