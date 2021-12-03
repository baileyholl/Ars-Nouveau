//package com.hollingsworth.arsnouveau.client.jei;
//
//import com.hollingsworth.arsnouveau.ArsNouveau;
//import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
//import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
//import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantmentRecipe;
//import com.hollingsworth.arsnouveau.api.recipe.GlyphPressRecipe;
//import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
//import com.hollingsworth.arsnouveau.common.potions.ModPotions;
//import com.hollingsworth.arsnouveau.common.spell.effect.EffectCrush;
//import com.hollingsworth.arsnouveau.setup.BlockRegistry;
//import mezz.jei.api.IModPlugin;
//import mezz.jei.api.JeiPlugin;
//import mezz.jei.api.constants.ModIds;
//import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
//import mezz.jei.api.registration.IRecipeCatalystRegistration;
//import mezz.jei.api.registration.IRecipeCategoryRegistration;
//import mezz.jei.api.registration.IRecipeRegistration;
//import net.minecraft.client.Minecraft;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.item.crafting.Recipe;
//import net.minecraft.world.item.crafting.RecipeManager;
//import net.minecraft.world.item.alchemy.PotionUtils;
//import net.minecraft.world.item.alchemy.Potions;
//import net.minecraft.resources.ResourceLocation;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//@JeiPlugin
//public class JEIArsNouveauPlugin implements IModPlugin {
//
//    @Override
//    public ResourceLocation getPluginUid() {
//        return new ResourceLocation(ArsNouveau.MODID, "main");
//    }
//
//    @Override
//    public void registerCategories(IRecipeCategoryRegistration registry) {
//        registry.addRecipeCategories(
//            new GlyphPressRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
//                new EnchantingApparatusRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
//                new CrushRecipeCategory(registry.getJeiHelpers().getGuiHelper())
//        );
//    }
//
//    @Override
//    public void registerRecipes(IRecipeRegistration registry) {
//
//
//        List<GlyphPressRecipe> recipeList = new ArrayList<>();
//        List<EnchantingApparatusRecipe> apparatus = new ArrayList<>();
//        List<CrushRecipe> crushRecipes = new ArrayList<>();
//        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
//        for(Recipe i : manager.getRecipes()){
//            if(i instanceof GlyphPressRecipe){
//                recipeList.add((GlyphPressRecipe) i);
//            }
//            if(i instanceof EnchantingApparatusRecipe && !(i instanceof EnchantmentRecipe)){
//                apparatus.add((EnchantingApparatusRecipe) i);
//            }
//            if(i instanceof CrushRecipe){
//                crushRecipes.add((CrushRecipe) i);
//            }
//        }
//        registry.addRecipes(recipeList, GlyphPressRecipeCategory.UID);
//
//
//        registry.addRecipes(apparatus, EnchantingApparatusRecipeCategory.UID);
//        registry.addRecipes(crushRecipes, CrushRecipeCategory.UID);
//        ItemStack manaPot = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.MANA_REGEN_POTION);
//        IJeiBrewingRecipe manaPotionRecipe = registry.getVanillaRecipeFactory().createBrewingRecipe(Collections.singletonList(new ItemStack(BlockRegistry.MANA_BERRY_BUSH)),
//                PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD), manaPot );
//        registry.addRecipes(Collections.singletonList(manaPotionRecipe), new ResourceLocation(ModIds.MINECRAFT_ID, "brewing"));
//    }
//
//    @Override
//    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
//        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.GLYPH_PRESS_BLOCK), GlyphPressRecipeCategory.UID);
//        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.ENCHANTING_APP_BLOCK), EnchantingApparatusRecipeCategory.UID);
//        registry.addRecipeCatalyst(new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(EffectCrush.INSTANCE)), CrushRecipeCategory.UID);
//    }
//}
