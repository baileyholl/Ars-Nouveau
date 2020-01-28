package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.api.AbstractSpellPart;
import com.hollingsworth.craftedmagic.spell.augment.AugmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public abstract class EffectType extends AbstractSpellPart {


    protected EffectType(String tag, String description) {
        super(tag, description);
    }

    // Apply the effect at the destination position.
    public abstract void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AugmentType> enhancements);


}
