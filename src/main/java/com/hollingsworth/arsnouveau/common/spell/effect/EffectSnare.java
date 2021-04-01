package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.List;

public class EffectSnare extends AbstractEffect {

    public EffectSnare() {
        super(GlyphLib.EffectSnareID, "Snare");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            Entity livingEntity = ((EntityRayTraceResult) rayTraceResult).getEntity();
            if(!(livingEntity instanceof LivingEntity))
                return;
            ((LivingEntity)livingEntity).addPotionEffect(new EffectInstance(Effects.SLOWNESS,  100  + 20 * getBuffCount(augments, AugmentExtendTime.class), 20));
            livingEntity.setMotion(0,0,0);
            livingEntity.velocityChanged = true;
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return livingEntityHitSuccess(rayTraceResult);
    }

    @Override
    public String getBookDescription() {
        return "Stops entities from moving and jumping. Extend Time will increase the duration of this effect.";
    }

    @Override
    public Item getCraftingReagent() {
        return Items.COBWEB;
    }

    @Override
    public int getManaCost() {
        return 80;
    }
}
