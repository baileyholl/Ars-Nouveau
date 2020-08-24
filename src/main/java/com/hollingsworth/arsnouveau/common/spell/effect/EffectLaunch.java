package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class EffectLaunch extends AbstractEffect {
    public EffectLaunch() {
        super(ModConfig.EffectLaunchID, "Launch");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AbstractAugment> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();
            entity.setMotion(entity.getMotion().add(0, 0.8 + .25*getAmplificationBonus(augments), 0));
            entity.velocityChanged = true;
            entity.fallDistance = 0.0f;
        }
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    @Override
    public int getManaCost() {
        return 30;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.SLIME_BALL;
    }

    @Override
    protected String getBookDescription() {
        return "Boosts a target into the air. Can be used for large jumps or for scaling mountains!";
    }
}
