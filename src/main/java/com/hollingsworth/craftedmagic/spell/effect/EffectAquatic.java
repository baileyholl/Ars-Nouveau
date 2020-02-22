package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractEffect;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;
import com.hollingsworth.craftedmagic.potions.ModPotions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectAquatic extends AbstractEffect {
    public EffectAquatic() {
        super(ModConfig.EffectAquatic, "Aquatic");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AbstractAugment> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
            applyPotion(((LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity()), Effects.CONDUIT_POWER, augments);
        }
    }

    @Override
    public int getManaCost() {
        return 25;
    }
}
