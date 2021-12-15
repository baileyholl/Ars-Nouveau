package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;

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
            Optional<AbstractSpellPart> spell =  ArsNouveauAPI.getInstance().getSpellpartMap().values().stream().filter(sp -> sp.getTag().equals(s.trim())).findFirst();
            spell.ifPresent(spells::add);
        });
        return spells;
    }

}
