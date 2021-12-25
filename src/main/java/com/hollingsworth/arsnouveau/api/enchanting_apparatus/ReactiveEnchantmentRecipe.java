package com.hollingsworth.arsnouveau.api.enchanting_apparatus;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ReactiveEnchantmentRecipe extends EnchantmentRecipe{
    public static final String RECIPE_ID = "reactive_enchantment";

    public ReactiveEnchantmentRecipe(List<Ingredient> pedestalItems, Enchantment enchantment, int level, int manaCost){
        super(pedestalItems, enchantment, level, manaCost);
    }


    @Override
    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile, @Nullable Player player) {
        ItemStack parchment = getParchment(pedestalItems);
        return super.isMatch(pedestalItems, reagent, enchantingApparatusTile, player) && !parchment.isEmpty() && !CasterUtil.getCaster(parchment).getSpell().isEmpty();
    }

    public static @Nonnull ItemStack getParchment(List<ItemStack> pedestalItems){
        for(ItemStack stack : pedestalItems){
            if(stack.getItem() instanceof SpellParchment){
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeType<?> getType() {
        return Registry.RECIPE_TYPE.get(new ResourceLocation(ArsNouveau.MODID, RECIPE_ID));
    }

    @Override
    public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        ItemStack resultStack = super.getResult(pedestalItems, reagent, enchantingApparatusTile);
        ItemStack parchment = getParchment(pedestalItems);
        ISpellCaster parchmentCaster = CasterUtil.getCaster(parchment);
        ReactiveCaster reactiveCaster = new ReactiveCaster(resultStack);
        reactiveCaster.setColor(parchmentCaster.getColor());
        reactiveCaster.setSpell(parchmentCaster.getSpell());
        return resultStack;
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(ArsNouveau.MODID, "reactive");
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<ReactiveEnchantmentRecipe> {

        @Override
        public ReactiveEnchantmentRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(GsonHelper.getAsString(json, "enchantment")));
            int level = GsonHelper.getAsInt(json, "level", 1);
            int manaCost = GsonHelper.getAsInt(json,"mana", 0);
            List<Ingredient> stacks = new ArrayList<>();
            for(int i = 1; i < 9; i++){
                if(json.has("item_"+i))
                    stacks.add(Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "item_" + i)));
            }
            return new ReactiveEnchantmentRecipe( stacks,enchantment,level, manaCost);
        }

        @Nullable
        @Override
        public ReactiveEnchantmentRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int length = buffer.readInt();
            String enchantID = buffer.readUtf();
            int level = buffer.readInt();
            int manaCost = buffer.readInt();
            List<Ingredient> stacks = new ArrayList<>();

            for(int i = 0; i < length; i++){
                try{ stacks.add(Ingredient.fromNetwork(buffer)); }catch (Exception e){
                    e.printStackTrace();
                    break;
                }
            }
            return new ReactiveEnchantmentRecipe(stacks, ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantID)), level, manaCost);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ReactiveEnchantmentRecipe recipe) {
            buf.writeInt(recipe.pedestalItems.size());
            buf.writeUtf(recipe.enchantment.getRegistryName().toString());
            buf.writeInt(recipe.enchantLevel);
            buf.writeInt(recipe.manaCost());
            for(Ingredient i : recipe.pedestalItems){
                i.toNetwork(buf);
            }
        }
    }
}
