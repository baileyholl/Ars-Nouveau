package com.hollingsworth.arsnouveau.api;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.ISpellTier;
import com.hollingsworth.arsnouveau.common.items.ItemsRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EnchantingApparatusRecipe {

    public ItemStack catalyst; // Used in the arcane pedestal
    public ItemStack result; // Result item
    public List<ItemStack> pedestalItems; // Items part of the recipe
    public String description;
    private String category;

    public EnchantingApparatusRecipe(ItemStack result, ItemStack catalyst, List<ItemStack> pedestalItems, String category){
        this.catalyst = catalyst;
        this.pedestalItems = pedestalItems;
        this.result = result;
        this.category = category;
    }

    public EnchantingApparatusRecipe(ItemStack result, ItemStack catalyst, ItemStack pedestalItems,  String category){
        this(result, catalyst, Collections.singletonList(pedestalItems), category);
    }

    public EnchantingApparatusRecipe(Item result, Item catalyst, Item[] pedestalItems, String category){
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for(Item i : pedestalItems){
            stacks.add(new ItemStack(i));
        }
        this.catalyst = new ItemStack(catalyst);
        this.result = new ItemStack(result);
        this.pedestalItems = stacks;
        this.category = category;
    }

    /**
     * A utility equals implementation that ignores the order of elements in the pedestal List.
     */
    public boolean isEqualTo(EnchantingApparatusRecipe other){
        return other.result == this.result && this.catalyst == other.catalyst && this.pedestalItems.size() == other.pedestalItems.size() && areSameSet(this.pedestalItems, other.pedestalItems);
    }
    /**
     * A utility method for checking if the crafting components of a recipe are the same.
     */
    @Nullable
    public ItemStack isResultOf(ItemStack catalyst, List<ItemStack> pedestalItems){
        System.out.println("Checking result");
        pedestalItems = pedestalItems.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());

        if (this.catalyst.getItem() != catalyst.getItem() || this.pedestalItems.size() != pedestalItems.size() || !areSameSet(pedestalItems, this.pedestalItems)) {

            return null;
        }

        return this.result;
    }

    // Function to check if both arrays are same
    static boolean areSameSet(List<ItemStack> A, List<ItemStack> B)
    {
        if(A.size() != B.size()) {
            return false;
        }
        A.sort(Comparator.comparing(a -> a.getItem().getName().getString()));
        B.sort(Comparator.comparing(a -> a.getItem().getName().getString()));

        for(int i = 0; i < A.size(); i++){
            if(A.get(i).getItem() != B.get(i).getItem()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnchantingApparatusRecipe that = (EnchantingApparatusRecipe) o;
        return Objects.equals(catalyst, that.catalyst) &&
                Objects.equals(pedestalItems, that.pedestalItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(catalyst, pedestalItems);
    }

    @Override
    public String toString() {
        return "EnchantingApparatusRecipe{" +
                "catalyst=" + catalyst +
                ", result=" + result +
                ", pedestalItems=" + pedestalItems +
                '}';
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
        infoPage.addProperty("reagent", this.catalyst.getItem().getRegistryName().toString());

//        String manaCost = this.getManaCost() < 20 ? "Low" : "Medium";
//        manaCost = this.getManaCost() > 50 ? "High" : manaCost;
//        infoPage.addProperty("mana_cost", manaCost);
        if(this.pedestalItems != null){
            AtomicInteger count = new AtomicInteger(1);
            this.pedestalItems.forEach(i ->{
                infoPage.addProperty("item" + count.get(), i.getItem().getRegistryName().toString());
                count.addAndGet(1);
            });
//            String clayType;
//            if(this.getTier() == ISpellTier.Tier.ONE){
//                clayType = ItemsRegistry.magicClay.getRegistryName().toString();
//            }else if(this.getTier() == ISpellTier.Tier.TWO){
//                clayType = ItemsRegistry.marvelousClay.getRegistryName().toString();
//            }else{
//                clayType = ItemsRegistry.mythicalClay.getRegistryName().toString();
//            }
//            infoPage.addProperty("clay_type", clayType);
//            infoPage.addProperty("reagent", this.getCraftingReagent().getRegistryName().toString());
        }


        jsonArray.add(descPage);
        jsonArray.add(infoPage);
        jsonobject.add("pages", jsonArray);
        return jsonobject;
    }




}
