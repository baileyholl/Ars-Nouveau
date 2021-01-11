package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.recipe.GlyphPressRecipe;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JeiPlugin
public class JEIArsNouveauPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ArsNouveau.MODID, "main");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(
            new GlyphPressRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new EnchantingApparatusRecipeCategory(registry.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {


        List<GlyphPressRecipe> recipeList = new ArrayList<>();
        List<EnchantingApparatusRecipe> apparatus = new ArrayList<>();
        RecipeManager manager = Minecraft.getInstance().world.getRecipeManager();
        for(IRecipe i : manager.getRecipes()){
            if(i instanceof GlyphPressRecipe){
                recipeList.add((GlyphPressRecipe) i);
            }
            if(i instanceof EnchantingApparatusRecipe){
                apparatus.add((EnchantingApparatusRecipe) i);
            }
        }
        registry.addRecipes(recipeList, GlyphPressRecipeCategory.UID);

//        int i = 0;
//        for(IEnchantingRecipe r : ArsNouveauAPI.getInstance().getEnchantingApparatusRecipes()){
//            if(!(r instanceof EnchantingApparatusRecipe))
//                continue;
//            EnchantingApparatusRecipe recipe = (EnchantingApparatusRecipe)r;
//            apparatus.add(new ApparatusRecipe(
//                    new ResourceLocation(ArsNouveau.MODID,"apparatus_" + i++),
//                            recipe.pedestalItems, recipe.catalyst, recipe.result, recipe.manaCost()));
//        }
        registry.addRecipes(apparatus, EnchantingApparatusRecipeCategory.UID);
        ItemStack manaPot = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModPotions.MANA_REGEN_POTION);
        IJeiBrewingRecipe manaPotionRecipe = registry.getVanillaRecipeFactory().createBrewingRecipe(Collections.singletonList(new ItemStack(BlockRegistry.MANA_BERRY_BUSH)),
                PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.AWKWARD), manaPot );
        registry.addRecipes(Collections.singletonList(manaPotionRecipe), new ResourceLocation(ModIds.MINECRAFT_ID, "brewing"));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.GLYPH_PRESS_BLOCK), GlyphPressRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.ENCHANTING_APP_BLOCK), EnchantingApparatusRecipeCategory.UID);
    }
}
