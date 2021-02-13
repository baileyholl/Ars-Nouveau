package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectLeash extends AbstractEffect {
    public EffectLeash() {
        super(GlyphLib.EffectLeashID, "Leash");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(shooter == null || !isRealPlayer(shooter))
            return;
        Vector3d vector3d = rayTraceResult.getHitVec();
        BlockPos hit = new BlockPos(vector3d.x, vector3d.y +1, vector3d.z);
        if(rayTraceResult instanceof EntityRayTraceResult){
            Entity e = ((EntityRayTraceResult) rayTraceResult).getEntity();
            if(e instanceof MobEntity){
                ((MobEntity) e).setLeashHolder(shooter, true);

                System.out.println("set");
               // LeadItem.bindPlayerMobs((PlayerEntity) shooter, world, hit);
            }
            System.out.println("hit");
        }
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
