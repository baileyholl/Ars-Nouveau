package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Deprecated
public class SpellRecipeUtil {

    @Deprecated // Marked for removal for Spell object methods.
    public static ArrayList<AbstractSpellPart> getSpellsFromString(String spellString){
        List<String> spellStrings = Arrays.asList(spellString.split(","));
        ArrayList<AbstractSpellPart> spells = new ArrayList<>();

        spellStrings.forEach(s->{
            Optional<AbstractSpellPart> spell =  ArsNouveauAPI.getInstance().getSpell_map().values().stream().filter(sp -> sp.getTag().equals(s.trim())).findFirst();
            spell.ifPresent(spells::add);
        });
        return spells;
    }

    /**
     * Parses the NBT stored string, which is stored as an array of spell IDs. ex: [touch, harm, , , ,]
     */
    @Deprecated // Marked for removal for Spell object methods.
    public static List<AbstractSpellPart> getSpellsFromTagString(String recipeStr){
        return Spell.deserialize(recipeStr).recipe;
    }

    @Deprecated // Marked for removal for Spell object methods.
    public static String serializeForNBT(List<AbstractSpellPart> abstractSpellPart){
        return new Spell(abstractSpellPart).serialize();
    }
    @Deprecated // Marked for removal
    public static String getDisplayString(List<AbstractSpellPart> abstractSpellPart){
        return new Spell(abstractSpellPart).getDisplayString();
    }
}
