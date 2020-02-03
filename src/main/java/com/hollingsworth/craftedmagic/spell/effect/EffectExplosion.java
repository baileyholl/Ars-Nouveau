package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.spell.augment.AugmentEmpower;
import com.hollingsworth.craftedmagic.spell.augment.AugmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectExplosion extends EffectType{
    public EffectExplosion() {
        super(ModConfig.EffectExplosion, "Explosion");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AugmentType> augments) {
        Vec3d vec = rayTraceResult.getHitVec();
        int intensity = getBuffCount(augments, AugmentEmpower.class);
        world.createExplosion(shooter, null, vec.x, vec.y, vec.z, intensity, false, Explosion.Mode.DESTROY);
    }

    @Override
    public int getManaCost() {
        return 35;
    }
}
