package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.api.AbstractSpellPart;
import com.hollingsworth.craftedmagic.spell.enhancement.EnhancementType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public abstract class EffectType extends AbstractSpellPart {

    public EffectType(String tag) {
        super(tag);
    }

    // Apply the effect at the destination position.
    public abstract void onResolve(RayTraceResult rayTraceResult, World world, EntityLivingBase shooter, ArrayList<EnhancementType> enhancements);
}
