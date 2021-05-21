package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil.getEquippedAugments;

public class Spell {

    public List<AbstractSpellPart> recipe;
    private int cost;

    public Spell(List<AbstractSpellPart> recipe){
        this.recipe = recipe == null ? new ArrayList<>() : recipe; // Safe check for tiles initializing a null
        this.cost = getInitialCost();
    }

    public Spell(){
        this.recipe = new ArrayList<>();
        this.cost = 0;
    }

    public int getSpellSize(){
        return recipe.size();
    }

    public List<AbstractAugment> getAugments(int startPosition, @Nullable LivingEntity caster){
        ArrayList<AbstractAugment> augments = new ArrayList<>();
        if(recipe == null || recipe.isEmpty())
            return augments;
        for(int j = startPosition + 1; j < recipe.size(); j++){
            AbstractSpellPart next_spell = recipe.get(j);
            if(next_spell instanceof AbstractAugment){
                augments.add((AbstractAugment) next_spell);
            }else{
                break;
            }
        }
        // Add augment bonuses from equipment
        if(caster != null)
            augments.addAll(getEquippedAugments(caster));
        return augments;
    }

    public int getBuffsAtIndex(int startPosition, @Nullable LivingEntity caster, Class<? extends AbstractAugment> augmentClass){
        return (int) getAugments(startPosition, caster).stream().filter(a -> a.getClass().equals(augmentClass)).count();
    }

    private int getInitialCost(){
        int cost = 0;
        if(recipe == null)
            return cost;
        for (int i = 0; i < recipe.size(); i++) {
            AbstractSpellPart spell = recipe.get(i);
            if (!(spell instanceof AbstractAugment)) {
                List<AbstractAugment> augments = getAugments(i, null);
                cost += spell.getAdjustedManaCost(augments);
            }
        }
        return cost;
    }

    public int getCastingCost(){
        return Math.max(0, cost);
    }

    public void setCost(int cost){
        this.cost = cost;
    }

    public boolean isEmpty(){
        return recipe == null || recipe.isEmpty();
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
        String lastStr = "";

        for(int i = 0; i < recipe.size(); i++){
            AbstractSpellPart spellPart = recipe.get(i);
            int num = 1;
            for(int j = i + 1; j < recipe.size(); j++){
                if(spellPart.name.equals(recipe.get(j).name))
                    num++;
                else
                    break;
            }
            if(num > 1){
                str.append(spellPart.getLocaleName()).append(" x").append(num);
                i += num - 1;
            }else{
                str.append(spellPart.getLocaleName());
            }
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
