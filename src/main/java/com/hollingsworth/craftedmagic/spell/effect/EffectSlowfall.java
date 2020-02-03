package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.spell.augment.AugmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectSlowfall extends EffectType{
    protected EffectSlowfall(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AugmentType> augments) {

    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
