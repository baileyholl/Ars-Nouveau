package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.augment.AugmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectDamage extends EffectType {

    public EffectDamage() {super(ModConfig.EffectDamageID, "Damage" ); }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AugmentType> enhancements) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            ((EntityRayTraceResult) rayTraceResult).getEntity().attackEntityFrom(DamageSource.causePlayerDamage((PlayerEntity) shooter), 5.0f);
        }
    }

    @Override
    public int getManaCost() {
        return 0;
    }

}
