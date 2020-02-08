package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.api.AbstractSpellPart;
import com.hollingsworth.craftedmagic.spell.augment.AugmentAmplify;
import com.hollingsworth.craftedmagic.spell.augment.AugmentDampen;
import com.hollingsworth.craftedmagic.spell.augment.AugmentExtendTime;
import com.hollingsworth.craftedmagic.spell.augment.AugmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class EffectType extends AbstractSpellPart {


    protected EffectType(String tag, String description) {
        super(tag, description);
    }

    // Apply the effect at the destination position.
    public abstract void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AugmentType> augments);

    public void applyPotion(LivingEntity entity, Effect potionEffect, ArrayList<AugmentType> augmentTypes){
        applyPotion(entity, potionEffect, augmentTypes, 30, 30);
    }

    public void applyPotion(LivingEntity entity, Effect potionEffect, ArrayList<AugmentType> augmentTypes, int baseDuration, int durationBuffBase){
        int duration = baseDuration + durationBuffBase * getBuffCount(augmentTypes, AugmentExtendTime.class);
        int amp = getBuffCount(augmentTypes, AugmentAmplify.class);
        entity.addPotionEffect(new EffectInstance(potionEffect, duration * 20, amp));
    }

    public int getBuffCount(ArrayList<AugmentType> augments, Class spellClass){
        return (int) augments.stream().filter(spellClass::isInstance).count();
    }

    public boolean hasBuff(ArrayList<AugmentType> augments, Class spellClass){
        return getBuffCount(augments, spellClass) > 0;
    }

    public int getAmplificationBonus(ArrayList<AugmentType> augmentTypes){
        return getBuffCount(augmentTypes, AugmentAmplify.class) - getBuffCount(augmentTypes, AugmentDampen.class);
    }

    public int getAdjustedManaCost(ArrayList<AugmentType> augmentTypes){
        int cost = getManaCost();
        for(AugmentType a: augmentTypes){
            if(a instanceof AugmentDampen && !dampenIsAllowed()){
                continue;
            }
            cost += a.getManaCost();
        }
        return Math.max(cost, 0);
    }

    // Check for mana reduction exploit
    public boolean dampenIsAllowed(){
        return false;
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
