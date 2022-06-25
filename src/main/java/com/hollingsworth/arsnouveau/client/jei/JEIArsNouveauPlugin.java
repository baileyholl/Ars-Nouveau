package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.SpellWriteRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCrush;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIArsNouveauPlugin implements IModPlugin {
    public static final RecipeType<GlyphRecipe> GLYPH_RECIPE_TYPE = RecipeType.create(ArsNouveau.MODID, "glyph_recipe", GlyphRecipe.class);
    public static final RecipeType<EnchantingApparatusRecipe> ENCHANTING_APP_RECIPE_TYPE = RecipeType.create(ArsNouveau.MODID, "enchanting_apparatus", EnchantingApparatusRecipe.class);
    public static final RecipeType<ImbuementRecipe> IMBUEMENT_RECIPE_TYPE = RecipeType.create(ArsNouveau.MODID, "imbuement", ImbuementRecipe.class);
    public static final RecipeType<CrushRecipe> CRUSH_RECIPE_TYPE = RecipeType.create(ArsNouveau.MODID, "crush", CrushRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ArsNouveau.MODID, "main");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(
            new GlyphRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new CrushRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new ImbuementRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new EnchantingApparatusRecipeCategory(registry.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        List<GlyphRecipe> recipeList = new ArrayList<>();
        List<EnchantingApparatusRecipe> apparatus = new ArrayList<>();
        List<CrushRecipe> crushRecipes = new ArrayList<>();
        List<ImbuementRecipe> imbuementRecipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeRegistry.IMBUEMENT_TYPE.get());
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        for(Recipe<?> i : manager.getRecipes()){
            if(i instanceof GlyphRecipe glyphRecipe){
                recipeList.add(glyphRecipe);
            }
            if(i instanceof EnchantingApparatusRecipe enchantingApparatusRecipe && !(i instanceof EnchantmentRecipe)  && !(i instanceof SpellWriteRecipe)){
                apparatus.add(enchantingApparatusRecipe);
            }
            if(i instanceof CrushRecipe crushRecipe){
                crushRecipes.add(crushRecipe);
            }
        }
        registry.addRecipes(GLYPH_RECIPE_TYPE, recipeList);
        registry.addRecipes(CRUSH_RECIPE_TYPE, crushRecipes);
        registry.addRecipes(ENCHANTING_APP_RECIPE_TYPE, apparatus);
        registry.addRecipes(IMBUEMENT_RECIPE_TYPE, imbuementRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.SCRIBES_BLOCK), GLYPH_RECIPE_TYPE);
        registry.addRecipeCatalyst(new ItemStack(EffectCrush.INSTANCE.glyphItem), CRUSH_RECIPE_TYPE);
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.IMBUEMENT_BLOCK), IMBUEMENT_RECIPE_TYPE);
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.ENCHANTING_APP_BLOCK), ENCHANTING_APP_RECIPE_TYPE);
    }
}