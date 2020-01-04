package com.hollingsworth.craftedmagic.spell.method;

import com.hollingsworth.craftedmagic.api.AbstractSpellPart;
import com.hollingsworth.craftedmagic.api.Position;
import com.hollingsworth.craftedmagic.spell.ISpellCallback;
import com.hollingsworth.craftedmagic.spell.SpellResolver;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public abstract class CastMethod extends AbstractSpellPart {
    public SpellResolver resolver;

    //The cast location
    public abstract void onCast(Position position, World world, EntityLivingBase shooter);

    public CastMethod(String tag){
        super(tag);
    }
}
