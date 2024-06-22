package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ArmorUpgradeRecipe extends EnchantingApparatusRecipe implements ITextOutput{

    public int tier; // 0 indexed

    public ArmorUpgradeRecipe(List<Ingredient> pedestalItems, int cost, int tier) {
        this(ArsNouveau.prefix( "upgrade_" + tier), pedestalItems, cost, tier);
    }

    public ArmorUpgradeRecipe(ResourceLocation id, List<Ingredient> pedestalItems, int cost, int tier) {
        this.pedestalItems = pedestalItems;
        this.id = id;
        this.sourceCost = cost;
        this.tier = tier;
    }

    @Override
    public boolean excludeJei() {
        return true;
    }

    @Override
    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "ars_nouveau:" + RecipeRegistry.ARMOR_RECIPE_ID);
        jsonobject.addProperty("sourceCost", getSourceCost());
        JsonArray pedestalArr = new JsonArray();
        for (Ingredient i : this.pedestalItems) {
            JsonObject object = new JsonObject();
            object.add("item", i.toJson());
            pedestalArr.add(object);
        }
        jsonobject.add("pedestalItems", pedestalArr);
        jsonobject.addProperty("tier", tier);
        return jsonobject;
    }

    @Override
    public boolean doesReagentMatch(ItemStack reag) {
        return true;
    }

    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile, @Nullable Player player) {
        IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(reagent);
        if(!(perkHolder instanceof ArmorPerkHolder armorPerkHolder)){
            return false;
        }
        return armorPerkHolder.getTier() == (tier - 1) && super.isMatch(pedestalItems, reagent, enchantingApparatusTile, player);
    }

    @Override
    public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(reagent);
        if(!(perkHolder instanceof ArmorPerkHolder armorPerkHolder)){
            return reagent.copy();
        }
        armorPerkHolder.setTier(tier);
        return reagent.copy();
    }


    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.ARMOR_UPGRADE_TYPE.get();
    }

    @Override
    public Component getOutputComponent() {
        return Component.translatable("ars_nouveau.armor_upgrade.book_desc", tier);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.ARMOR_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<ArmorUpgradeRecipe> {

        @Override
        public ArmorUpgradeRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            int cost = json.has("sourceCost") ? GsonHelper.getAsInt(json, "sourceCost") : 0;
            int tier = json.has("tier") ? GsonHelper.getAsInt(json, "tier") : 0;
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
            return new ArmorUpgradeRecipe(recipeId, stacks, cost, tier);
        }

        @Nullable
        @Override
        public ArmorUpgradeRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {

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
            int cost = buffer.readInt();
            int tier = buffer.readInt();
            return new ArmorUpgradeRecipe(recipeId, stacks, cost, tier);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ArmorUpgradeRecipe recipe) {
            buf.writeInt(recipe.pedestalItems.size());
            for (Ingredient i : recipe.pedestalItems) {
                i.toNetwork(buf);
            }
            buf.writeInt(recipe.sourceCost);
            buf.writeInt(recipe.tier);
        }
    }
}
