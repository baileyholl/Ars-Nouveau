package com.hollingsworth.craftedmagic.api.spell;
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

    public String getBookDescription(){return this.description;}

    public ISpellTier.Tier getTier(){
        return ISpellTier.Tier.ONE;
    }
}
