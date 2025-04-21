package com.hollingsworth.arsnouveau.api;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.recipe.PotionIngredient;
import com.hollingsworth.arsnouveau.api.scrying.IScryer;
import com.hollingsworth.arsnouveau.api.spell.ISpellValidator;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.common.spell.validation.StandardSpellValidator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main class of the Ars Nouveau API.
 * <p>
 * Obtain an instance with {@link ArsNouveauAPI#getInstance()}.
 * FOR ADDON AUTHORS: All registration should occur from YOUR MOD CONSTRUCTOR. BEFORE ITEMS REGISTER.
 * If you need to access data from other addons, use the FMLLoadingComplete event.
 */
public class ArsNouveauAPI {

    //This is intended as a dev debug tool, used in mana bars. Do not make into a config option with a PR. A Starkiller will be dispatched if you do.
    public static boolean ENABLE_DEBUG_NUMBERS;

    private ConcurrentHashMap<ResourceLocation, IScryer> scryerMap = new ConcurrentHashMap<>();

    private Set<RecipeType<? extends IEnchantingRecipe>> enchantingRecipeTypes = ConcurrentHashMap.newKeySet();

    /**
     * Validator to use when crafting a spell in the spell book.
     */
    private ISpellValidator craftingSpellValidator;
    /**
     * Validator to use when casting a spell.
     */
    private ISpellValidator castingSpellValidator;

    private List<RecipeHolder<? extends IEnchantingRecipe>> enchantingApparatusRecipes = new ArrayList<>();

    private List<BrewingRecipe> brewingRecipes = new ArrayList<>();

    public Set<RecipeType<? extends IEnchantingRecipe>> getEnchantingRecipeTypes() {
        return enchantingRecipeTypes;
    }


    public List<RecipeHolder<? extends IEnchantingRecipe>> getEnchantingApparatusRecipes(Level world) {
        List<RecipeHolder<? extends IEnchantingRecipe>> recipes = new ArrayList<>(enchantingApparatusRecipes);
        RecipeManager manager = world.getRecipeManager();
        List<RecipeHolder<? extends IEnchantingRecipe>> recipesByType = new ArrayList<>(); // todo lazy init enchanting types
        for (RecipeType<? extends IEnchantingRecipe> type : enchantingRecipeTypes) {
            recipesByType.addAll(manager.getAllRecipesFor(type));
        }
        recipes.addAll(recipesByType);
        return recipes;
    }

    public List<BrewingRecipe> getAllPotionRecipes(Level world) {
        if (brewingRecipes.isEmpty()) {
            world.potionBrewing().getRecipes().forEach(ib -> {
                if (ib instanceof BrewingRecipe brewingRecipe)
                    brewingRecipes.add(brewingRecipe);
            });
            for(PotionBrewing.Mix<Potion> mix : world.potionBrewing().potionMixes){
                brewingRecipes.add(new BrewingRecipe(
                        PotionIngredient.fromPotion(mix.from()),
                        mix.ingredient(),
                        PotionIngredient.fromPotion(mix.to()).getItems()[0]
                ));
            }
        }
        return brewingRecipes;
    }

    /**
     * Returns the {@link ISpellValidator} that enforces the standard rules for spell crafting.
     * This validator relaxes the rule about starting with a cast method, to allow for spells that will be imprinted
     * onto caster items, which generally have a built-in cast method.
     */
    public ISpellValidator getSpellCraftingSpellValidator() {
        if (craftingSpellValidator == null)
            craftingSpellValidator = new StandardSpellValidator(false);
        return craftingSpellValidator;
    }

    /**
     * Returns the {@link ISpellValidator} that enforces the standard rules for spells at cast time.
     * This validator enforces all rules, asserting that a spell can be cast.
     */
    public ISpellValidator getSpellCastingSpellValidator() {
        if (castingSpellValidator == null) // Lazy init this because we need configs to load.
            castingSpellValidator = new StandardSpellValidator(true);
        return castingSpellValidator;
    }

    public @Nullable IScryer getScryer(ResourceLocation id) {
        return this.scryerMap.get(id);
    }

    public boolean registerScryer(IScryer scryer) {
        this.scryerMap.put(scryer.getRegistryName(), scryer);
        return true;
    }

    public void onResourceReload(){
        this.brewingRecipes = new ArrayList<>();
    }

    private ArsNouveauAPI() {
    }

    /**
     * Retrieves a handle to the singleton instance.
     */
    public static ArsNouveauAPI getInstance() {
        return INSTANCE;
    }

    // This is needed internally by the mod, so just make it eagerly.
    private static final ArsNouveauAPI INSTANCE = new ArsNouveauAPI();

    static {
        //think we just gotta ensure the path exists once
        FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(ArsNouveau.MODID));
    }
}
