package com.hollingsworth.craftedmagic.api.util;

import com.hollingsworth.craftedmagic.api.spell.AbstractSpellPart;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;

import java.util.ArrayList;

public class SpellRecipeUtil {

    public static ArrayList<AbstractAugment> getAugments(ArrayList<AbstractSpellPart> spell_recipe, int startPosition){
        ArrayList<AbstractAugment> augments = new ArrayList<>();
        for(int j = startPosition + 1; j < spell_recipe.size(); j++){
            AbstractSpellPart next_spell = spell_recipe.get(j);
            if(next_spell instanceof AbstractAugment){
                augments.add((AbstractAugment) next_spell);
            }else{
                break;
            }
        }
        return augments;
    }
}
