package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectKnockback extends AbstractEffect {

    public EffectKnockback() {
        super(GlyphLib.EffectKnockbackID, "Knockback");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
            LivingEntity target = (LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity();
            float strength = 1.5f + getAmplificationBonus(augments);
            knockback(target, shooter, strength);
            target.velocityChanged = true;
        }
    }

    public void knockback(LivingEntity target, LivingEntity entityKnockingAway, float strength){
        target.applyKnockback(strength,MathHelper.sin(entityKnockingAway.rotationYaw * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(entityKnockingAway.rotationYaw * ((float)Math.PI / 180F))));
    }


    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return livingEntityHitSuccess(rayTraceResult);
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    @Override
    public int getManaCost() {
        return 15;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.PISTON;
    }

    @Override
    public String getBookDescription() {
        return "Knocks a target away a short distance from the caster";
    }
}
