package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Spell implements Cloneable{
    public static final Spell EMPTY = new Spell();

    public List<AbstractSpellPart> recipe = new ArrayList<>();
    private int cost;

    public Spell(List<AbstractSpellPart> recipe){
        this.recipe = recipe == null ? new ArrayList<>() : recipe; // Safe check for tiles initializing a null
        this.cost = getInitialCost();
    }

    public Spell(){ }

    public Spell(AbstractSpellPart... spellParts){
        super();
        add(spellParts);
    }

    public Spell add(AbstractSpellPart spellPart){
        recipe.add(spellPart);
        return this;
    }

    public Spell add(AbstractSpellPart... spellParts){
        for(AbstractSpellPart part : spellParts)
            add(part);
        return this;
    }

    public Spell add(AbstractSpellPart spellPart, int count){
        for(int i = 0; i < count; i++)
            recipe.add(spellPart);
        return this;
    }

    public int getSpellSize(){
        return recipe.size();
    }

    public @Nullable AbstractCastMethod getCastMethod(){
        if(this.recipe == null || this.recipe.isEmpty())
            return null;
        return this.recipe.get(0) instanceof AbstractCastMethod ? (AbstractCastMethod) recipe.get(0) : null;

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

        return augments;
    }

    public int getInstanceCount(AbstractSpellPart spellPart){
        int count = 0;
        for (AbstractSpellPart abstractSpellPart : this.recipe) {
            if (abstractSpellPart.equals(spellPart))
                count++;
        }
        return count;
    }

    public int getBuffsAtIndex(int startPosition, @Nullable LivingEntity caster, AbstractAugment augment){
        return (int) getAugments(startPosition, caster).stream().filter(a -> a.equals(augment)).count();
    }

    private int getInitialCost(){
        int cost = 0;
        if(recipe == null)
            return cost;
        for (AbstractSpellPart spell : recipe) {
            cost += spell.getConfigCost();
        }
        return Math.max(0, cost);
    }

    public int getCastingCost(){
        return Math.max(0, cost);
    }

    public void setCost(int cost){
        this.cost = Math.max(0, cost);
    }

    public boolean isEmpty(){
        return recipe == null || recipe.isEmpty();
    }

    public String serialize(){
        List<String> tags = new ArrayList<>();
        for(AbstractSpellPart slot : recipe){
            tags.add(slot.getRegistryName());
        }
        return tags.toString();
    }

    public static Spell deserialize(String recipeStr){
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        if (recipeStr.length() <= 3) // Account for empty strings and '[,]'
            return new Spell(recipe);
        String[] recipeList = recipeStr.substring(1, recipeStr.length() - 1).split(",");
        for(String id : recipeList){
            if (ArsNouveauAPI.getInstance().getSpellpartMap().containsKey(id.trim()))
                recipe.add(ArsNouveauAPI.getInstance().getSpellpartMap().get(id.trim()));
        }
        return new Spell(recipe);
    }

    public String getDisplayString(){
        StringBuilder str = new StringBuilder();

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
        return !this.isEmpty();
    }

    public Spell add(AbstractSpellPart spellPart, int count, int index){
        for(int i = 0; i < count; i++)
            recipe.add(index, spellPart);
        return this;
    }

    @Override
    public Spell clone() {
        try {
            Spell clone = (Spell) super.clone();
            clone.recipe = new ArrayList<>(this.recipe);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
