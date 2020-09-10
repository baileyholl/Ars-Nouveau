package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractEffect extends AbstractSpellPart {


    public AbstractEffect(String tag, String description) {
        super(tag, description);
    }

    // Apply the effect at the destination position.
    public abstract void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments);

    public void applyPotion(LivingEntity entity, Effect potionEffect, List<AbstractAugment> augmentTypes){
        applyPotion(entity, potionEffect, augmentTypes, 30, 30);
    }

    public void applyPotion(LivingEntity entity, Effect potionEffect, List<AbstractAugment> augmentTypes, int baseDuration, int durationBuffBase){
        int duration = baseDuration + durationBuffBase * getBuffCount(augmentTypes, AugmentExtendTime.class);
        int amp = getBuffCount(augmentTypes, AugmentAmplify.class);
        entity.addPotionEffect(new EffectInstance(potionEffect, duration * 20, amp));
    }

    public Map<Class, Integer> getAllowedAugments(){
        return new HashMap<>();
    }

    public Map<Class, Integer> buildAmpMap(){
        Map<Class, Integer> map = new HashMap();
        map.put(AugmentDampen.class, 10);
        map.put(AugmentAmplify.class, 10);
        return map;
    }
}
