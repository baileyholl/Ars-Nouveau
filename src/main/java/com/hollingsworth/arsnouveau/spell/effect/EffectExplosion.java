package com.hollingsworth.arsnouveau.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.spell.augment.AugmentExtract;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectExplosion extends AbstractEffect {

    public EffectExplosion() {
        super(ModConfig.EffectExplosionID, "Explosion");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AbstractAugment> augments) {
        System.out.println(rayTraceResult);
        if(rayTraceResult == null)
            return;
        Vec3d vec = rayTraceResult.getHitVec();
        float intensity = 0.75f + getBuffCount(augments, AugmentAmplify.class);
        int dampen = getBuffCount(augments, AugmentDampen.class);
        intensity -= 0.5 * dampen;
        Explosion.Mode mode = hasBuff(augments, AugmentDampen.class) ? Explosion.Mode.NONE  : Explosion.Mode.DESTROY;
        mode = hasBuff(augments, AugmentExtract.class) ? Explosion.Mode.BREAK : mode;
        world.createExplosion(shooter,  vec.x, vec.y, vec.z, intensity,  mode);
    }

    @Override
    public int getManaCost() {
        return 35;
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }
}
