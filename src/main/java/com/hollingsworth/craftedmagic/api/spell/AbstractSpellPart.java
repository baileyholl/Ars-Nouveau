package com.hollingsworth.craftedmagic.api.spell;

import com.hollingsworth.craftedmagic.spell.augment.AugmentDampen;

import java.util.ArrayList;

public abstract class AbstractSpellPart implements ISpellTier{

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

    public ISpellTier.Tier getTier(){
        return ISpellTier.Tier.ONE;
    }


}
