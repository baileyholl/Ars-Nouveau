package com.hollingsworth.arsnouveau.api.recipe;

import WeightedMobType;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import record;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SummonRitualRecipe implements Recipe<Container> {

    public final ResourceLocation id;
    public final Ingredient catalyst;
    public final MobSource mobSource;
    public final int count;
    public ArrayList<WeightedMobType> mobs;

    public SummonRitualRecipe(ResourceLocation id, Ingredient catalyst, MobSource source, int count, ArrayList<WeightedMobType> mobs) {
        this.id = id;
        this.catalyst = catalyst;
        this.mobSource = source;
        this.count = count;
        this.mobs = mobs;
    }

    public SummonRitualRecipe(ResourceLocation id, Ingredient catalyst, MobSource source, int count) {
        this.id = id;
        this.catalyst = catalyst;
        this.mobSource = source;
        this.count = count;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return false;
    }

    public boolean matches(List<ItemStack> augments) {
        return EnchantingApparatusRecipe.doItemsMatch(augments, Arrays.stream(this.catalyst.getItems()).map(Ingredient::of).toList());
    }

    @Override
    public ItemStack assemble(Container p_44001_, RegistryAccess p_267165_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
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
        return RecipeRegistry.SUMMON_RITUAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.SUMMON_RITUAL_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "ars_nouveau:summon_ritual");
        JsonArray mobs = new JsonArray();
        this.mobs.forEach(mob -> {
            mobs.add(mob.toJson());
        });
        jsonobject.add("mobs", mobs);
        jsonobject.addProperty("source", this.mobSource.toString());
        jsonobject.addProperty("count", this.count);
        jsonobject.add("augment", catalyst.toJson());
        return jsonobject;
    }

    public static class Serializer implements RecipeSerializer<SummonRitualRecipe> {

        @Override
        public SummonRitualRecipe fromJson(ResourceLocation pRecipeId, JsonObject json) {
            Ingredient augment = Ingredient.fromJson(GsonHelper.isArrayNode(json, "augment") ? GsonHelper.getAsJsonArray(json, "augment") : GsonHelper.getAsJsonObject(json, "augment"));
            MobSource source = MobSource.valueOf(GsonHelper.getAsString(json, "source", "MOB_LIST"));
            ArrayList<WeightedMobType> mobs = new ArrayList<>();
            if (json.has("mob")) {
                mobs.add(new WeightedMobType(ResourceLocation.tryParse(json.get("mob").getAsString())));
            }
            if (json.has("mobs")) {
                GsonHelper.getAsJsonArray(json, "mobs").forEach(el -> {
                    mobs.add(WeightedMobType.fromJson(el.getAsJsonObject()));
                });
            }
            int count = GsonHelper.getAsInt(json, "count", 1);

            return new SummonRitualRecipe(pRecipeId, augment, source, count, mobs);
        }

        @Override
        public @Nullable SummonRitualRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient catalyst = Ingredient.fromNetwork(pBuffer);
            MobSource source = pBuffer.readEnum(MobSource.class);
            int count = pBuffer.readInt();
            ArrayList<WeightedMobType> mobs = pBuffer.readCollection(Lists::newArrayListWithCapacity, new WeightedMobType.Reader());

            return new SummonRitualRecipe(pRecipeId, catalyst, source, count, mobs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, SummonRitualRecipe pRecipe) {
            pRecipe.catalyst.toNetwork(pBuffer);
            pBuffer.writeEnum(pRecipe.mobSource);
            pBuffer.writeInt(pRecipe.count);
            pBuffer.writeCollection(pRecipe.mobs, new WeightedMobType.Writer());
        }
    }


    // A possible expansion of the summon ritual recipe to allow for multiple mob types to be spawned
    public final List<WeightedMobType> mobTypes = new ArrayList<>();
    /**
     * A mob type with a weight and a chance to be selected for spawning
     *
     * @param mob    The mob to spawn
     * @param weight If there is more than one mob in the list, this is the chance that this mob will be selected
     */
    public record WeightedMobType(ResourceLocation mob, int weight) implements WeightedEntry {

        public WeightedMobType(ResourceLocation mob) {
            this(mob, 1);
        }

        public JsonObject toJson() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("mob", this.mob.toString());
            jsonobject.addProperty("weight", this.weight);
            return jsonobject;
        }

        public static WeightedMobType fromJson(JsonObject json) {
            return new WeightedMobType(ResourceLocation.tryParse(GsonHelper.getAsString(json, "mob")), GsonHelper.getAsInt(json, "weight"));
        }

        @Override
        public @NotNull Weight getWeight() {
            return Weight.of(this.weight);
        }

        public static class Writer implements FriendlyByteBuf.Writer<WeightedMobType> {
            @Override
            public void accept(FriendlyByteBuf friendlyByteBuf, WeightedMobType weightedMobType) {
                friendlyByteBuf.writeResourceLocation(weightedMobType.mob);
                friendlyByteBuf.writeInt(weightedMobType.weight);
            }
        }

        public static class Reader implements FriendlyByteBuf.Reader<WeightedMobType> {
            @Override
            public WeightedMobType apply(FriendlyByteBuf friendlyByteBuf) {
                return new WeightedMobType(friendlyByteBuf.readResourceLocation(), friendlyByteBuf.readInt());
            }
        }
    }

    public enum MobSource {
        CURRENT_BIOME,
        MOB_LIST
    }

}
