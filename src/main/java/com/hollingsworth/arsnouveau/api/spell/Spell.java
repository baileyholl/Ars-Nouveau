package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;

import java.util.ArrayList;
import java.util.List;

public class Spell {

    public final List<AbstractSpellPart> recipe;

    public Spell(List<AbstractSpellPart> recipe){
        this.recipe = recipe;
    }

    public int getSpellSize(){
        return recipe.size();
    }

    public int getCost(){
        int cost = 0;
        for (int i = 0; i < recipe.size(); i++) {
            AbstractSpellPart spell = recipe.get(i);
            if (!(spell instanceof AbstractAugment)) {

                List<AbstractAugment> augments = SpellRecipeUtil.getAugments(recipe, i, null);
                cost += spell.getAdjustedManaCost(augments);
            }
        }
        return cost;
    }

    public String serialize(){
        List<String> tags = new ArrayList<>();
        for(AbstractSpellPart slot : recipe){
            tags.add(slot.tag);
        }
        return tags.toString();
    }

    public static Spell deserialize(String recipeStr){
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        if (recipeStr.length() <= 3) // Account for empty strings and '[,]'
            return new Spell(recipe);
        String[] recipeList = recipeStr.substring(1, recipeStr.length() - 1).split(",");
        for(String id : recipeList){
            if (ArsNouveauAPI.getInstance().getSpell_map().containsKey(id.trim()))
                recipe.add(ArsNouveauAPI.getInstance().getSpell_map().get(id.trim()));
        }
        return new Spell(recipe);
    }

    public String getDisplayString(){
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < recipe.size(); i++) {
            AbstractSpellPart spellPart = recipe.get(i);
            str.append(spellPart.name);
            if(i < recipe.size() - 1){
                str.append(" -> ");
            }
        }
        return str.toString();
    }

    public boolean isValid(){
        return this.recipe != null && !this.recipe.isEmpty();
    }
}
