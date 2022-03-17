package com.hollingsworth.arsnouveau.api;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.recipe.PotionIngredient;
import com.hollingsworth.arsnouveau.api.recipe.VanillaPotionRecipe;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.ritual.IScryer;
import com.hollingsworth.arsnouveau.api.ritual.RitualContext;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellValidator;
import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import com.hollingsworth.arsnouveau.common.items.FamiliarScript;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.spell.validation.StandardSpellValidator;
import com.hollingsworth.arsnouveau.setup.Config;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Main class of the Ars Nouveau API.
 *
 * Obtain an instance with {@link ArsNouveauAPI#getInstance()}.
 * FOR ADDON AUTHORS: All registration should occur from the {@link com.hollingsworth.arsnouveau.api.event.ArsNouveauAPIEvent.Init event}.
 * That event is fired after mod loading. Adding to these maps during mod loading IS NOT THREAD SAFE.
 */
public class ArsNouveauAPI {

    /**
     * Map of all spells to be registered in the spell system
     *
     * key: Unique spell ID. Please make this snake_case!
     * value: Associated glyph
     */
    private HashMap<String, AbstractSpellPart> spellpartMap = new HashMap<>();

    private HashMap<String, AbstractRitual> ritualMap = new HashMap<>();

    private HashMap<String, AbstractFamiliarHolder> familiarHolderMap = new HashMap<>();

    /**
     * Contains the list of glyph item instances.
     */
    private HashMap<String, Glyph> glyphItemMap = new HashMap<>();

    private HashMap<String, FamiliarScript> familiarScriptMap = new HashMap<>();

    /**
     * Contains the list of parchment item instances created during registration
     */
    private HashMap<String, RitualTablet> ritualParchmentMap = new HashMap<>();

    private HashMap<String, IScryer> scryerMap = new HashMap<>();

    /** Validator to use when crafting a spell in the spell book. */
    private ISpellValidator craftingSpellValidator;
    /** Validator to use when casting a spell. */
    private ISpellValidator castingSpellValidator;

    private List<IEnchantingRecipe> enchantingApparatusRecipes = new ArrayList<>();

    public List<VanillaPotionRecipe> vanillaPotionRecipes = new ArrayList<>();

    private List<BrewingRecipe> brewingRecipes = new ArrayList<>();
    /**
     * Spells that all spellbooks contain
     */
    private List<AbstractSpellPart> startingSpells = new ArrayList<>();

    public List<AbstractSpellPart> getDefaultStartingSpells(){
        return spellpartMap.values().stream().filter(Config::isStarterEnabled).collect(Collectors.toList());
    }

    public boolean addStartingSpell(String tag){
        if(ArsNouveauAPI.getInstance().getSpellpartMap().containsKey(tag)){
            return startingSpells.add(ArsNouveauAPI.getInstance().getSpellpartMap().get(tag));
        }else{
            throw new IllegalStateException("Attempted to add a starting spell for an unregistered spell. Spells must be added to the Spell Map first!");
        }
    }

    public Item getGlyphItem(String glyphName){
        for(Item i : ItemsRegistry.RegistrationHandler.ITEMS){
            if(i.getRegistryName().equals(new ResourceLocation(ArsNouveau.MODID, getSpellRegistryName(glyphName)))){
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
        glyphItemMap.put(id, new Glyph(getSpellRegistryName(id), part));
        return spellpartMap.put(id, part);
    }

    public AbstractSpellPart registerSpell(AbstractSpellPart part){
        return registerSpell(part.getId(), part);
    }


    /**
     * A registration helper for addons. Adds mana costs into the fallback cost map.
     */
    public AbstractSpellPart registerSpell(String id, AbstractSpellPart part, int manaCost){
        Config.addonSpellCosts.put(id, manaCost);
        return registerSpell(id, part);
    }

    public AbstractRitual registerRitual(String id, AbstractRitual ritual){
        ritualParchmentMap.put(id, new RitualTablet(getRitualRegistryName(id), ritual));
        return ritualMap.put(id, ritual);
    }

    public AbstractFamiliarHolder registerFamiliar(AbstractFamiliarHolder familiar){
        this.familiarScriptMap.put(familiar.id, new FamiliarScript(familiar));
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

    public @Nullable AbstractRitual getRitual(String id, RitualBrazierTile tile, RitualContext context){
        AbstractRitual ritual = getRitual(id);
        if(ritual != null){
            ritual.tile = tile;
            ritual.setContext(context);
        }
        return ritual;
    }

    public String getSpellRegistryName(String id){
        return "glyph_"+ id.toLowerCase();
    }

    public String getRitualRegistryName(String id){
        return "ritual_"+ id.toLowerCase();
    }

    public Map<String, AbstractSpellPart> getSpellpartMap() {
        return spellpartMap;
    }

    public Map<String, Glyph> getGlyphItemMap(){
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

    public List<IEnchantingRecipe> getEnchantingApparatusRecipes(Level world) {
        List<IEnchantingRecipe> recipes = new ArrayList<>(enchantingApparatusRecipes);
        RecipeManager manager = world.getRecipeManager();
        for(Recipe i : manager.getRecipes()){
            if(i instanceof IEnchantingRecipe){
                recipes.add((IEnchantingRecipe) i);
            }
        }
        return recipes;
    }

    public List<BrewingRecipe> getAllPotionRecipes(){
        if(brewingRecipes.isEmpty()){
            BrewingRecipeRegistry.getRecipes().forEach(ib ->{
                if(ib instanceof BrewingRecipe)
                    brewingRecipes.add((BrewingRecipe) ib);
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

    private ArsNouveauAPI(){}

    /** Retrieves a handle to the singleton instance. */
    public static ArsNouveauAPI getInstance() {
        return ARS_NOUVEAU_API;
    }

    // This is needed internally by the mod, so just make it eagerly.
    private static final ArsNouveauAPI ARS_NOUVEAU_API = new ArsNouveauAPI();
}
