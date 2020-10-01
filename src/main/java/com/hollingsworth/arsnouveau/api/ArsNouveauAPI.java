package com.hollingsworth.arsnouveau.api;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;

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

    private ArrayList<IEnchantingRecipe> enchantingApparatusRecipes;
    /**
     * Spells that all spellbooks contain
     */
    private ArrayList<AbstractSpellPart> startingSpells;

    public ArrayList<AbstractSpellPart> getStartingSpells(){
        return startingSpells;
    }

    public boolean addStartingSpell(String tag){
        if(ArsNouveauAPI.getInstance().getSpell_map().containsKey(tag)){
            return startingSpells.add(ArsNouveauAPI.getInstance().getSpell_map().get(tag));
        }else{
            throw new RuntimeException("Attempted to add a starting spell for an unregistered spell. Spells must be added to the Spell Map first!");
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

    public HashMap<String, AbstractSpellPart> getSpell_map() {
        return spell_map;
    }

    public HashMap<String, Glyph> getGlyphMap(){
        return glyphMap;
    }

    public ArrayList<IEnchantingRecipe> getEnchantingApparatusRecipes() { return enchantingApparatusRecipes; }

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
