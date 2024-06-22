package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.api.enchanting_apparatus.ReactiveEnchantmentRecipe.getParchment;

public class SpellWriteRecipe extends EnchantingApparatusRecipe implements ITextOutput {

    public SpellWriteRecipe(ResourceLocation id, List<Ingredient> pedestalItems, int cost) {
        this.pedestalItems = pedestalItems;
        this.id = id;
        this.sourceCost = cost;
    }

    public SpellWriteRecipe(List<Ingredient> pedestalItems) {
        this.pedestalItems = pedestalItems;
        this.id = ArsNouveau.prefix( "spell_write");
    }

    @Override
    public boolean excludeJei() {
        return true;
    }

    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile, @Nullable Player player) {
        ItemEnchantments enchantments = reagent.get(DataComponents.ENCHANTMENTS);
        int level = enchantments.getLevel(EnchantmentRegistry.REACTIVE_ENCHANTMENT.get());
        ItemStack parchment = getParchment(pedestalItems);
        return !parchment.isEmpty() && !CasterUtil.getCaster(parchment).getSpell().isEmpty() && level > 0 && super.isMatch(pedestalItems, reagent, enchantingApparatusTile, player);
    }

    @Override
    public boolean doesReagentMatch(ItemStack reag) {
        return true;
    }

    @Override
    public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        ItemStack parchment = getParchment(pedestalItems);
        ISpellCaster caster = CasterUtil.getCaster(parchment);
        ReactiveCaster reactiveCaster = new ReactiveCaster(reagent);
        reactiveCaster.setSpell(caster.getSpell());
        reactiveCaster.setColor(caster.getColor());
        return reagent.copy();
    }

    @Override
    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "ars_nouveau:" + RecipeRegistry.SPELL_WRITE_RECIPE_ID);
        jsonobject.addProperty("sourceCost", getSourceCost());
        JsonArray pedestalArr = new JsonArray();
        for (Ingredient i : this.pedestalItems) {
            JsonObject object = new JsonObject();
            object.add("item", i.toJson());
            pedestalArr.add(object);
        }
        jsonobject.add("pedestalItems", pedestalArr);
        return jsonobject;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.SPELL_WRITE_TYPE.get();
    }

    @Override
    public Component getOutputComponent() {
        return Component.translatable("ars_nouveau.spell_write.book_desc");
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.SPELL_WRITE_RECIPE.get();
    }


    public static class Serializer implements RecipeSerializer<SpellWriteRecipe> {

        @Override
        public SpellWriteRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            int cost = json.has("sourceCost") ? GsonHelper.getAsInt(json, "sourceCost") : 0;
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
            return new SpellWriteRecipe(recipeId, stacks, cost);
        }

        @Nullable
        @Override
        public SpellWriteRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
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
            return new SpellWriteRecipe(recipeId, stacks, cost);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SpellWriteRecipe recipe) {
            buf.writeInt(recipe.pedestalItems.size());
            for (Ingredient i : recipe.pedestalItems) {
                i.toNetwork(buf);
            }
            buf.writeInt(recipe.sourceCost);
        }
    }
}
