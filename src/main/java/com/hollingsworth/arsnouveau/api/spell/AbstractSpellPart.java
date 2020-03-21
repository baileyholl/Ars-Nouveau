package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.spell.augment.AugmentDampen;

import java.util.ArrayList;

public abstract class AbstractSpellPart implements ISpellTier, Comparable<AbstractSpellPart> {

    public abstract int getManaCost();
    public String tag;
    public String description;
    /*Tag for NBT data and SpellManager#spellList*/
    public String getTag(){
        return this.tag;
    }

    public String getIcon(){return this.tag + ".png";}

    protected AbstractSpellPart(String tag, String description){
        this.tag = tag;
        this.description = description;
    }

    public int getAdjustedManaCost(ArrayList<AbstractAugment> augmentTypes){
        int cost = getManaCost();
        for(AbstractAugment a: augmentTypes){
            if(a instanceof AugmentDampen && !dampenIsAllowed()){
                continue;
            }
            cost += a.getManaCost();
        }
        return Math.max(cost, 0);
    }

    // Check for mana reduction exploit
    public boolean dampenIsAllowed(){
        return false;
    }

    public String getBookDescription(){return this.description;}

    public ISpellTier.Tier getTier() {
        return ISpellTier.Tier.ONE;
    }

    public int getBuffCount(ArrayList<AbstractAugment> augments, Class spellClass){
        return (int) augments.stream().filter(spellClass::isInstance).count();
    }

    public boolean hasBuff(ArrayList<AbstractAugment> augments, Class spellClass){
        return getBuffCount(augments, spellClass) > 0;
    }

    public int getAmplificationBonus(ArrayList<AbstractAugment> augmentTypes){
        return getBuffCount(augmentTypes, AugmentAmplify.class) - getBuffCount(augmentTypes, AugmentDampen.class);
    }

    @Override
    public int compareTo(AbstractSpellPart o) {
        return this.getTier().ordinal() - o.getTier().ordinal();
    }
}
