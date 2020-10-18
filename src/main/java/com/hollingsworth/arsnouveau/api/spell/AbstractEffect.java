package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
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


    public Vector3d safelyGetHitPos(RayTraceResult result){
        if(result instanceof EntityRayTraceResult)
            return ((EntityRayTraceResult) result).getEntity() != null ? ((EntityRayTraceResult) result).getEntity().getPositionVec() : result.getHitVec();
        return result.getHitVec();
    }

}
