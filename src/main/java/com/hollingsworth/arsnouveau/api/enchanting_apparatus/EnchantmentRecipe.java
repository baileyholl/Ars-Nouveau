package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.registries.ForgeRegistries;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class EnchantmentRecipe extends EnchantingApparatusRecipe {
    public Enchantment enchantment;
    public int enchantLevel;


    public EnchantmentRecipe(List<Ingredient> pedestalItems, Enchantment enchantment, int level, int manaCost) {
        this.pedestalItems = pedestalItems;
        this.enchantment = enchantment;
        this.enchantLevel = level;
        this.sourceCost = manaCost;
        this.id = ArsNouveau.prefix( getRegistryName(enchantment).getPath() + "_" + level);
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.ENCHANTMENT_TYPE.get();
    }

    public boolean doesReagentMatch(ItemStack stack, Player playerEntity) {
        if (stack.isEmpty())
            return false;
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        int level = enchantments.getOrDefault(enchantment, 0);
        Collection<Enchantment> enchantList = EnchantmentHelper.getEnchantments(stack).keySet();
        enchantList.remove(enchantment);
        if (stack.getItem() != Items.BOOK && stack.getItem() != Items.ENCHANTED_BOOK && !enchantment.canEnchant(stack)) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.enchanting.incompatible"));
            return false;
        }

        if (!EnchantmentHelper.isEnchantmentCompatible(enchantList, enchantment)) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.enchanting.incompatible"));
            return false;
        }

        if (!(this.enchantLevel - level == 1)) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.enchanting.bad_level"));
            return false;
        }

        return true;
    }

    @Override
    public boolean excludeJei() {
        return true;
    }

    // Override and move reagent match to the end, so we can give feedback
    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile, @Nullable Player player) {
        pedestalItems = pedestalItems.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
        return this.pedestalItems.size() == pedestalItems.size() && doItemsMatch(pedestalItems, this.pedestalItems) && doesReagentMatch(reagent, player);
    }

    @Override
    public boolean doesReagentMatch(ItemStack stack) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        int level = enchantments.getOrDefault(enchantment, 0);
        return enchantment.canEnchant(stack) && this.enchantLevel - level == 1 && EnchantmentHelper.isEnchantmentCompatible(EnchantmentHelper.getEnchantments(stack).keySet(), enchantment);
    }

    @Override
    public ItemStack assemble(EnchantingApparatusTile inv, RegistryAccess p_267165_) {
        ItemStack stack = inv.getStack().getItem() == Items.BOOK ? new ItemStack(Items.ENCHANTED_BOOK) : inv.getStack().copy();
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        enchantments.put(enchantment, enchantLevel);
        EnchantmentHelper.setEnchantments(enchantments, stack);
        return stack;
    }

    @Override
    public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile tile) {
        return assemble(tile, null);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.ENCHANTMENT_SERIALIZER.get();
    }

    @Override
    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "ars_nouveau:" + RecipeRegistry.ENCHANTMENT_RECIPE_ID);
        jsonobject.addProperty("enchantment", getRegistryName(enchantment).toString());
        jsonobject.addProperty("level", enchantLevel);
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

    public static class Serializer implements RecipeSerializer<EnchantmentRecipe> {

        @Override
        public EnchantmentRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(GsonHelper.getAsString(json, "enchantment")));
            int level = GsonHelper.getAsInt(json, "level", 1);
            int manaCost = GsonHelper.getAsInt(json, "sourceCost", 0);
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
            return new EnchantmentRecipe(stacks, enchantment, level, manaCost);
        }

        @Nullable
        @Override
        public EnchantmentRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int length = buffer.readInt();
            String enchantID = buffer.readUtf();
            int level = buffer.readInt();
            int manaCost = buffer.readInt();
            List<Ingredient> stacks = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                try {
                    stacks.add(Ingredient.fromNetwork(buffer));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            return new EnchantmentRecipe(stacks, ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantID)), level, manaCost);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, EnchantmentRecipe recipe) {
            buf.writeInt(recipe.pedestalItems.size());
            buf.writeUtf(getRegistryName(recipe.enchantment).toString());
            buf.writeInt(recipe.enchantLevel);
            buf.writeInt(recipe.getSourceCost());
            for (Ingredient i : recipe.pedestalItems) {
                i.toNetwork(buf);
            }
        }
    }
}
