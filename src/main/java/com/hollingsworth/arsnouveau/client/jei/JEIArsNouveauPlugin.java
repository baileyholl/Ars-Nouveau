package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.SpellWriteRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCrush;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

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
            new GlyphRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new EnchantingApparatusRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new CrushRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new ImbuementRecipeCategory(registry.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {


        List<GlyphRecipe> recipeList = new ArrayList<>();
        List<EnchantingApparatusRecipe> apparatus = new ArrayList<>();
        List<CrushRecipe> crushRecipes = new ArrayList<>();
        List<ImbuementRecipe> imbuementRecipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeRegistry.IMBUEMENT_TYPE);
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        for(Recipe i : manager.getRecipes()){
            if(i instanceof GlyphRecipe){
                recipeList.add((GlyphRecipe) i);
            }
            if(i instanceof EnchantingApparatusRecipe && !(i instanceof EnchantmentRecipe)  && !(i instanceof SpellWriteRecipe)){
                apparatus.add((EnchantingApparatusRecipe) i);
            }
            if(i instanceof CrushRecipe){
                crushRecipes.add((CrushRecipe) i);
            }
        }
        registry.addRecipes(recipeList, GlyphRecipeCategory.UID);


        registry.addRecipes(apparatus, EnchantingApparatusRecipeCategory.UID);
        registry.addRecipes(crushRecipes, CrushRecipeCategory.UID);
        registry.addRecipes(imbuementRecipes, ImbuementRecipeCategory.UID);
        ItemStack manaPot = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.MANA_REGEN_POTION);
        IJeiBrewingRecipe manaPotionRecipe = registry.getVanillaRecipeFactory().createBrewingRecipe(Collections.singletonList(new ItemStack(BlockRegistry.SOURCEBERRY_BUSH)),
                PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD), manaPot );
        registry.addRecipes(Collections.singletonList(manaPotionRecipe), new ResourceLocation(ModIds.MINECRAFT_ID, "brewing"));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.SCRIBES_BLOCK), GlyphRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.ENCHANTING_APP_BLOCK), EnchantingApparatusRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(EffectCrush.INSTANCE)), CrushRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.IMBUEMENT_BLOCK), ImbuementRecipeCategory.UID);
    }
}
