package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.List;

public class EffectLeap extends AbstractEffect {
    public EffectLeap() {
        super(GlyphLib.EffectLeapID, "Leap");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
            EntityRayTraceResult rayTraceResult1 = (EntityRayTraceResult) rayTraceResult;
            LivingEntity e = (LivingEntity) rayTraceResult1.getEntity();

            double bonus = 1.5 + getAmplificationBonus(augments);
            e.setDeltaMovement(e.getLookAngle().x * bonus, e.getLookAngle().y * bonus, e.getLookAngle().z * bonus);
            e.fallDistance = 0;
            e.hurtMarked = true;
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return livingEntityHitSuccess(rayTraceResult);
    }

    @Override
    public String getBookDescription() {
        return "Launches the target in the direction they are looking. Amplification will increase the distance moved.";
    }

    @Override
    public Item getCraftingReagent() {
        return ItemsRegistry.WILDEN_WING;
    }

    @Override
    public int getManaCost() {
        return 20;
    }
}
