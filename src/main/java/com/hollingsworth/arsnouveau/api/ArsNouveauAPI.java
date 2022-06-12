package com.hollingsworth.arsnouveau.api;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.recipe.PotionIngredient;
import com.hollingsworth.arsnouveau.api.recipe.VanillaPotionRecipe;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.ritual.IScryer;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellValidator;
import com.hollingsworth.arsnouveau.common.items.FamiliarScript;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.spell.validation.StandardSpellValidator;
import com.hollingsworth.arsnouveau.setup.Config;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Main class of the Ars Nouveau API.
 *
 * Obtain an instance with {@link ArsNouveauAPI#getInstance()}.
 * FOR ADDON AUTHORS: All registration should occur from YOUR MOD CONSTRUCTOR. BEFORE ITEMS REGISTER.
 * If you need to access data from other addons, use the FMLLoadingComplete event.
 */
public class ArsNouveauAPI {

    public static ResourceLocation getRegistryName(Item i){
        return ForgeRegistries.ITEMS.getKey(i);
    }
    public static ResourceLocation getRegistryName(Enchantment e){
        return ForgeRegistries.ENCHANTMENTS.getKey(e);
    }


    /**
     * Map of all spells to be registered in the spell system
     *
     * key: Unique spell ID. Please make this snake_case!
     * value: Associated glyph
     */
    private ConcurrentHashMap<String, AbstractSpellPart> spellpartMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, AbstractRitual> ritualMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, AbstractFamiliarHolder> familiarHolderMap = new ConcurrentHashMap<>();

    /**
     * Contains the list of glyph item instances.
     */
    private ConcurrentHashMap<String, Supplier<Glyph>> glyphItemMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, FamiliarScript> familiarScriptMap = new ConcurrentHashMap<>();

    /**
     * Contains the list of parchment item instances created during registration
     */
    private ConcurrentHashMap<String, RitualTablet> ritualParchmentMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, IScryer> scryerMap = new ConcurrentHashMap<>();

    private Set<RecipeType<? extends IEnchantingRecipe>> enchantingRecipeTypes = ConcurrentHashMap.newKeySet();

    private ConcurrentHashMap<ResourceLocation, SpellSound> spellSoundsRegistry = new ConcurrentHashMap<>();

    /** Validator to use when crafting a spell in the spell book. */
    private ISpellValidator craftingSpellValidator;
    /** Validator to use when casting a spell. */
    private ISpellValidator castingSpellValidator;

    private List<IEnchantingRecipe> enchantingApparatusRecipes = new ArrayList<>();

    public List<VanillaPotionRecipe> vanillaPotionRecipes = new ArrayList<>();

    private List<BrewingRecipe> brewingRecipes = new ArrayList<>();

    public List<AbstractSpellPart> getDefaultStartingSpells(){
        return spellpartMap.values().stream().filter(Config::isStarterEnabled).collect(Collectors.toList());
    }

    public Item getGlyphItem(String glyphName){
        for(Item i : ItemsRegistry.RegistrationHandler.ITEMS){
            if(getRegistryName(i).equals(new ResourceLocation(ArsNouveau.MODID, getSpellRegistryName(glyphName)))){
                return i;
            }
        }
        return null;
    }

    public Item getGlyphItem(AbstractSpellPart spell){
        return getGlyphItem(spell.getId());
    }

    public Item getFamiliarItem(String id){
        return familiarScriptMap.get(id);
    }

    public AbstractSpellPart registerSpell(String id, AbstractSpellPart part){
        glyphItemMap.put(id, part::getGlyph);

        //register the spell part's config in
        ForgeConfigSpec spec;
        ForgeConfigSpec.Builder spellBuilder = new ForgeConfigSpec.Builder();
        part.buildConfig(spellBuilder);
        spec = spellBuilder.build();
        part.CONFIG = spec;
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, part.CONFIG, ArsNouveau.MODID + "/" + part.getId() +".toml");

        return spellpartMap.put(id, part);
    }

    static{
        //think we just gotta ensure the path exists once
        FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(ArsNouveau.MODID), ArsNouveau.MODID);
    }

    public AbstractSpellPart registerSpell(AbstractSpellPart part){
        return registerSpell(part.getId(), part);
    }

    public AbstractRitual registerRitual(String id, AbstractRitual ritual){
//        ritualParchmentMap.put(id, new RitualTablet(getRitualRegistryName(id), ritual));
        return ritualMap.put(id, ritual);
    }

    public AbstractFamiliarHolder registerFamiliar(AbstractFamiliarHolder familiar){
//        this.familiarScriptMap.put(familiar.id, new FamiliarScript(familiar));
        return familiarHolderMap.put(familiar.id, familiar);
    }

    public @Nullable AbstractRitual getRitual(String id){
        if(!ritualMap.containsKey(id))
            return null;
        try{
            return ritualMap.get(id).getClass().newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Deprecated // To be removed and replaced with resource locations
    public String getSpellRegistryName(String id){
        return "glyph_"+ id.toLowerCase();
    }

    public String getRitualRegistryName(String id){
        return "ritual_"+ id.toLowerCase();
    }

    public Map<String, AbstractSpellPart> getSpellpartMap() {
        return spellpartMap;
    }

    public Map<String, Supplier<Glyph>> getGlyphItemMap(){
        return glyphItemMap;
    }

    public Map<String, AbstractRitual> getRitualMap(){
        return ritualMap;
    }

    public Map<String, RitualTablet> getRitualItemMap(){
        return ritualParchmentMap;
    }

    public Map<String, AbstractFamiliarHolder> getFamiliarHolderMap(){
        return this.familiarHolderMap;
    }

    public Map<String, FamiliarScript> getFamiliarScriptMap(){
        return this.familiarScriptMap;
    }

    public Set<RecipeType<? extends IEnchantingRecipe>> getEnchantingRecipeTypes() {
        return enchantingRecipeTypes;
    }

    public List<IEnchantingRecipe> getEnchantingApparatusRecipes(Level world) {
        List<IEnchantingRecipe> recipes = new ArrayList<>(enchantingApparatusRecipes);
        RecipeManager manager = world.getRecipeManager();
        List<IEnchantingRecipe> recipesByType = new ArrayList<>(); // todo lazy init enchanting types
        for(RecipeType<? extends IEnchantingRecipe> type : enchantingRecipeTypes){
            recipesByType.addAll(manager.getAllRecipesFor(type));
        }
        recipes.addAll(recipesByType);
        return recipes;
    }

    public List<BrewingRecipe> getAllPotionRecipes(){
        if(brewingRecipes.isEmpty()){
            BrewingRecipeRegistry.getRecipes().forEach(ib ->{
                if(ib instanceof BrewingRecipe brewingRecipe)
                    brewingRecipes.add(brewingRecipe);
            });

            vanillaPotionRecipes.forEach(vanillaPotionRecipe -> {
                BrewingRecipe recipe = new BrewingRecipe(
                        PotionIngredient.fromPotion(vanillaPotionRecipe.potionIn),
                        Ingredient.of(vanillaPotionRecipe.reagent),
                        PotionIngredient.fromPotion(vanillaPotionRecipe.potionOut).getStack()
                );
                brewingRecipes.add(recipe);
            });

        }
        return brewingRecipes;
    }

    /**
     * Returns the {@link ISpellValidator} that enforces the standard rules for spell crafting.
     * This validator relaxes the rule about starting with a cast method, to allow for spells that will be imprinted
     * onto caster items, which generally have a built-in cast method.
     */
    public ISpellValidator getSpellCraftingSpellValidator() {
        if(craftingSpellValidator == null)
            craftingSpellValidator = new StandardSpellValidator(false);
        return craftingSpellValidator;
    }

    /**
     * Returns the {@link ISpellValidator} that enforces the standard rules for spells at cast time.
     * This validator enforces all rules, asserting that a spell can be cast.
     */
    public ISpellValidator getSpellCastingSpellValidator() {
        if(castingSpellValidator == null) // Lazy init this because we need configs to load.
            castingSpellValidator = new StandardSpellValidator(true);
        return castingSpellValidator;
    }

    public @Nullable IScryer getScryer(String id){
        return this.scryerMap.get(id);
    }

    public boolean registerScryer(IScryer scryer){
        this.scryerMap.put(scryer.getID(), scryer);
        return true;
    }

    public ConcurrentHashMap<ResourceLocation, SpellSound> getSpellSoundsRegistry(){
        return this.spellSoundsRegistry;
    }

    public SpellSound registerSpellSound(SpellSound sound){
        return this.spellSoundsRegistry.put(sound.getId(), sound);
    }

    private ArsNouveauAPI(){}

    /** Retrieves a handle to the singleton instance. */
    public static ArsNouveauAPI getInstance() {
        return ARS_NOUVEAU_API;
    }

    // This is needed internally by the mod, so just make it eagerly.
    private static final ArsNouveauAPI ARS_NOUVEAU_API = new ArsNouveauAPI();
}
