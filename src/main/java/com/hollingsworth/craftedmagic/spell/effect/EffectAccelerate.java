package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.spell.enhancement.EnhancementType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectAccelerate extends EffectType{
    protected EffectAccelerate(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<EnhancementType> enhancements) {

    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
