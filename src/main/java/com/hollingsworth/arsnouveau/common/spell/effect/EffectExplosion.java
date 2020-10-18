package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtract;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectExplosion extends AbstractEffect {

    public EffectExplosion() {
        super(ModConfig.EffectExplosionID, "Explosion");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult == null)
            return;
        Vec3d vec = rayTraceResult instanceof EntityRayTraceResult ? ((EntityRayTraceResult) rayTraceResult).getEntity().getPositionVec() : rayTraceResult.getHitVec();
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

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.TNT;
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Override
    protected String getBookDescription() {
        return "Causes an explosion at the location.";
    }
}
