package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectPull extends AbstractEffect {

    public EffectPull() {
        super(GlyphLib.EffectPullID, "Pull");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            Entity target = ((EntityRayTraceResult) rayTraceResult).getEntity();
            Vector3d vec3d = new Vector3d(shooter.getPosX() - target.getPosX(), shooter.getPosY() - target.getPosY(), shooter.getPosZ() - target.getPosZ());
            double d1 = 7;

            double d2 = 1.0D + 0.5 * getAmplificationBonus(augments);
            //target.setMotion(target.getMotion().add(vec3d.normalize().scale(d2 * d2 * 0.1D)));
            target.setMotion(target.getMotion().add(vec3d.normalize().scale(d2 )));
            target.velocityChanged = true;
            //target.move(MoverType.PLAYER, target.getMotion());
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return rayTraceResult instanceof EntityRayTraceResult;
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
        return Items.FISHING_ROD;
    }

    @Override
    public String getBookDescription() {
        return "Pulls the target closer to the caster";
    }
}
