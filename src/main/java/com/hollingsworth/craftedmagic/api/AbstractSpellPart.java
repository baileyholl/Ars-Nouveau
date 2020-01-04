package com.hollingsworth.craftedmagic.api;

import com.hollingsworth.craftedmagic.entity.EntityProjectileSpell;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public abstract class AbstractSpellPart {

    public abstract int getManaCost();
    public String tag;
    /*Tag for NBT data and SpellManager#spellList*/
    public String getTag(){
        return this.tag;
    }

    public String getIcon(){return this.tag + ".png";}
    public AbstractSpellPart(String tag){
        this.tag = tag;
    }


}
