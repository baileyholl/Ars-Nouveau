package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.api.AbstractSpellPart;
import com.hollingsworth.craftedmagic.spell.augment.AugmentEmpower;
import com.hollingsworth.craftedmagic.spell.augment.AugmentExtendTime;
import com.hollingsworth.craftedmagic.spell.augment.AugmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.swing.*;
import java.util.ArrayList;

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
        int amp = getBuffCount(augmentTypes, AugmentEmpower.class);
        entity.addPotionEffect(new EffectInstance(potionEffect, duration * 20, amp));
    }

    public int getBuffCount(ArrayList<AugmentType> augments, Class spellClass){
        return (int) augments.stream().filter(spellClass::isInstance).count();
    }
}
