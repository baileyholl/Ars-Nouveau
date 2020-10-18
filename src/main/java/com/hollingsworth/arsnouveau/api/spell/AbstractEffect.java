package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.List;

public abstract class AbstractEffect extends AbstractSpellPart {


    public AbstractEffect(String tag, String description) {
        super(tag, description);
    }

    // Apply the effect at the destination position.
    public abstract void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext);

    public void applyPotion(LivingEntity entity, Effect potionEffect, List<AbstractAugment> augmentTypes){
        applyPotion(entity, potionEffect, augmentTypes, 30, 30);
    }

    public void applyPotion(LivingEntity entity, Effect potionEffect, List<AbstractAugment> augmentTypes, int baseDuration, int durationBuffBase){
        int duration = baseDuration + durationBuffBase * getBuffCount(augmentTypes, AugmentExtendTime.class);
        int amp = getBuffCount(augmentTypes, AugmentAmplify.class);
        entity.addPotionEffect(new EffectInstance(potionEffect, duration * 20, amp));
    }

    public Vec3d safelyGetHitPos(RayTraceResult result){
        if(result instanceof EntityRayTraceResult)
            return ((EntityRayTraceResult) result).getEntity() != null ? ((EntityRayTraceResult) result).getEntity().getPositionVec() : result.getHitVec();
        return result.getHitVec();
    }

    // If the spell would actually do anything. Can be used for logic checks for things like the whelp.
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext){
        return true;
    }

    public boolean nonAirBlockSuccess(RayTraceResult rayTraceResult, World world){
        return rayTraceResult instanceof BlockRayTraceResult && world.getBlockState(((BlockRayTraceResult) rayTraceResult).getPos()).getMaterial() != Material.AIR;
    }

    public boolean livingEntityHitSuccess(RayTraceResult rayTraceResult){
        return rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity;
    }

    public boolean nonAirAnythingSuccess(RayTraceResult result, World world){
        return nonAirBlockSuccess(result, world) || livingEntityHitSuccess(result);
    }
}
