package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

import com.hollingsworth.arsnouveau.api.spell.ISpellTier.Tier;

public class EffectColdSnap extends AbstractEffect {
    public EffectColdSnap() {
        super(GlyphLib.EffectColdSnapID, "Cold Snap");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();
            if(!(entity instanceof LivingEntity))
                return;
            LivingEntity livingEntity = (LivingEntity) entity;
            Vector3d vec = safelyGetHitPos(rayTraceResult);
            float damage = 6.0f + 2.5f*getAmplificationBonus(augments);
            int range = 3 + getBuffCount(augments, AugmentAOE.class);
            int snareSec = 5 + getDurationModifier(augments);
            if(livingEntity.isInWaterOrRain() || livingEntity.getEffect(Effects.MOVEMENT_SLOWDOWN) != null){
                dealDamage(world, shooter, damage, augments, livingEntity, buildDamageSource(world, shooter).setMagic());
                ((ServerWorld)world).sendParticles(ParticleTypes.SPIT, vec.x, vec.y +0.5, vec.z,50,
                        ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1),ParticleUtil.inRange(-0.1, 0.1), 0.3);
                livingEntity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20 * snareSec, 20));
                for(Entity e : world.getEntities(shooter, new AxisAlignedBB(
                        livingEntity.blockPosition().north(range).east(range).above(range),  livingEntity.blockPosition().south(range).west(range).below(range)))){
                    if(e.equals(livingEntity) || !(e instanceof LivingEntity))
                        continue;
                    if(((LivingEntity) e).getEffect(Effects.MOVEMENT_SLOWDOWN) != null || e.isInWaterOrRain()){
                        dealDamage(world, shooter, damage, augments, e, buildDamageSource(world, shooter).setMagic());
                        vec = e.position();
                        ((ServerWorld)world).sendParticles(ParticleTypes.SPIT, vec.x, vec.y +0.5, vec.z,50,
                                ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1),ParticleUtil.inRange(-0.1, 0.1), 0.3);

                        ((LivingEntity) e).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20 * snareSec, 20));
                    }else{
                        ((LivingEntity) e).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20*snareSec, getAmplificationBonus(augments)));
                    }
                }
            }
        }
    }

    @Override
    public int getManaCost() {
        return 30;
    }

    @Override
    public String getBookDescription() {
        return "Snares and causes a burst of damage to an entity that is afflicted by Slowness or is wet. Nearby enemies that are not afflicted by Slow will be slowed. Nearby Enemies afflicted by Slow or wetness will also be hit by Cold Snap. Can be augmented using Amplify, Extend Time, and AOE.";
    }

    @Override
    public Item getCraftingReagent() {
        return ArsNouveauAPI.getInstance().getGlyphItem(new EffectFreeze());
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }
}