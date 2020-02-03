package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.augment.AugmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectFish extends EffectType{
    public EffectFish() {
        super(ModConfig.EffectAquatic, "Aquatic");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AugmentType> augments) {

    }

    @Override
    public int getManaCost() {
        return 25;
    }
}
