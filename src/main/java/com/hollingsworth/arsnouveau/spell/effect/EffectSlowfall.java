package com.hollingsworth.arsnouveau.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectSlowfall extends AbstractEffect {
    public EffectSlowfall() {
        super(ModConfig.EffectSlowfallID, "Slowfall");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AbstractAugment> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
            applyPotion(((LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity()), Effects.SLOW_FALLING, augments);
        }
    }

    @Override
    public int getManaCost() {
        return 25;
    }

    @Override
    public Tier getTier() {
        return Tier.ONE;
    }
}
