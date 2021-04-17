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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

import com.hollingsworth.arsnouveau.api.spell.ISpellTier.Tier;

public class EffectFlare extends AbstractEffect {
    public EffectFlare() {
        super(GlyphLib.EffectFlareID, "Flare");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();
            if(!(entity instanceof LivingEntity))
                return;
            LivingEntity livingEntity = (LivingEntity) entity;
            Vector3d vec = safelyGetHitPos(rayTraceResult);
            float damage = 6.0f + 3.0f*getAmplificationBonus(augments);
            int range = 3 + getBuffCount(augments, AugmentAOE.class);
            int fireSec = 5 + getDurationModifier(augments);
            if(livingEntity.isOnFire()){
                dealDamage(world, shooter, damage, augments, livingEntity, buildDamageSource(world, shooter).setIsFire().bypassArmor());
                ((ServerWorld)world).sendParticles(ParticleTypes.FLAME, vec.x, vec.y +0.5, vec.z,50,
                        ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1),ParticleUtil.inRange(-0.1, 0.1), 0.3);
                for(Entity e : world.getEntities(shooter, new AxisAlignedBB(
                        livingEntity.blockPosition().north(range).east(range).above(range),  livingEntity.blockPosition().south(range).west(range).below(range)))){
                    if(e.equals(livingEntity) || !(e instanceof LivingEntity))
                        continue;
                    dealDamage(world, shooter, damage, augments, e, buildDamageSource(world, shooter).setIsFire().bypassArmor());
                    e.setSecondsOnFire(fireSec);
                    vec = e.position();
                    ((ServerWorld)world).sendParticles(ParticleTypes.FLAME, vec.x, vec.y +0.5, vec.z,50,
                            ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1),ParticleUtil.inRange(-0.1, 0.1), 0.3);
                }
            }
        }
    }

    @Override
    public int getManaCost() {
        return 40;
    }

    @Override
    public String getBookDescription() {
        return "When used on entities that are on fire, Flare causes a burst of damage and will spread fire and deal damage to other nearby entities. Does significantly more damage than Harm. Can be augmented with Extend Time, Amplify, and AOE.";
    }

    @Override
    public Item getCraftingReagent() {
        return ArsNouveauAPI.getInstance().getGlyphItem(new EffectIgnite());
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }
}
