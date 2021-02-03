package com.hollingsworth.arsnouveau.api;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.api.recipe.GlyphPressRecipe;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellTier;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.setup.Config;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArsNouveauAPI {


    public enum PatchouliCategories{
        spells,
        machines,
        equipment,
        resources,
        getting_started,
        automation
    }

    /**
     * Map of all spells to be registered in the spell system
     *
     * key: Unique spell ID. Please make this snake_case!
     * value: Associated spell
     */
    private HashMap<String, AbstractSpellPart> spell_map;

    /**
     * Contains the list of glyph item instances used by the glyph press.
     */
    private HashMap<String, Glyph> glyphMap;

    private List<IEnchantingRecipe> enchantingApparatusRecipes;
    /**
     * Spells that all spellbooks contain
     */
    private List<AbstractSpellPart> startingSpells;

    public List<AbstractSpellPart> getDefaultStartingSpells(){
        return startingSpells.stream().filter(Config::isStarterEnabled).collect(Collectors.toList());
    }

    public boolean addStartingSpell(String tag){
        if(ArsNouveauAPI.getInstance().getSpell_map().containsKey(tag)){
            return startingSpells.add(ArsNouveauAPI.getInstance().getSpell_map().get(tag));
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
        return getGlyphItem(spell.tag);
    }

    /**
     * Returns the glyph that belongs to the crafting reagent given
     */
    public Glyph hasCraftingReagent(Item item){
        return getGlyphMap().values().stream().filter(a->a.spellPart.getCraftingReagent() == item).findFirst().orElse(null);
    }

    public AbstractSpellPart registerSpell(String id, AbstractSpellPart part){
        glyphMap.put(id, new Glyph(getSpellRegistryName(id), part));
        return spell_map.put(id, part);
    }

    public String getSpellRegistryName(String id){
        return "glyph_"+ id.toLowerCase();
    }

    public Map<String, AbstractSpellPart> getSpell_map() {
        return spell_map;
    }

    public Map<String, Glyph> getGlyphMap(){
        return glyphMap;
    }


    public List<IEnchantingRecipe> getEnchantingApparatusRecipes() {
        return enchantingApparatusRecipes;
    }

    public List<IEnchantingRecipe> getEnchantingApparatusRecipes(World world) {
        List<IEnchantingRecipe> recipes = new ArrayList<>(enchantingApparatusRecipes);
        RecipeManager manager = world.getRecipeManager();
        for(IRecipe i : manager.getRecipes()){
            if(i instanceof EnchantingApparatusRecipe){
                EnchantingApparatusRecipe recipe = (EnchantingApparatusRecipe) i;
                recipes.add(new EnchantingApparatusRecipe(recipe.result.copy(), recipe.reagent, recipe.pedestalItems, "custom"));
            }
        }
        return recipes;
    }

    public GlyphPressRecipe getGlyphPressRecipe(World world, Item reagent, @Nullable ISpellTier.Tier tier){
        if(reagent == null || reagent == Items.AIR)
            return null;

        RecipeManager manager = world.getRecipeManager();
        for(IRecipe i : manager.getRecipes()){
            if(i instanceof GlyphPressRecipe){
                if(((GlyphPressRecipe) i).reagent.getItem() == reagent && ((GlyphPressRecipe) i).tier == tier)
                    return (GlyphPressRecipe) i;
            }
        }
        return null;
    }

    private ArsNouveauAPI(){
        spell_map = new HashMap<>();
        glyphMap = new HashMap<>();
        startingSpells = new ArrayList<>();
        enchantingApparatusRecipes = new ArrayList<>();
    }

    public static ArsNouveauAPI getInstance(){
        if(arsNouveauAPI == null)
            arsNouveauAPI = new ArsNouveauAPI();
        return arsNouveauAPI;
    }

    private static ArsNouveauAPI arsNouveauAPI = null;
}
