package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectFlare extends AbstractEffect {
    public static EffectFlare INSTANCE = new EffectFlare();

    private EffectFlare() {
        super(GlyphLib.EffectFlareID, "Flare");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity entity = rayTraceResult.getEntity();
        if(!(entity instanceof LivingEntity))
            return;
        LivingEntity livingEntity = (LivingEntity) entity;
        Vec3 vec = safelyGetHitPos(rayTraceResult);
        float damage = (float) (DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier());
        int range = 3 + spellStats.getBuffCount(AugmentAOE.INSTANCE);
        int fireSec = (int) (5.0 + EXTEND_TIME.get() * spellStats.getDurationMultiplier());
        DamageSource source =  buildDamageSource(world, shooter).setIsFire();
        if(livingEntity.isOnFire()){
            dealDamage(world, shooter, damage, spellStats, livingEntity,source);
            ((ServerLevel)world).sendParticles(ParticleTypes.FLAME, vec.x, vec.y +0.5, vec.z,50,
                    ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1),ParticleUtil.inRange(-0.1, 0.1), 0.3);
            for(Entity e : world.getEntities(shooter, new AABB(
                    livingEntity.blockPosition().north(range).east(range).above(range),  livingEntity.blockPosition().south(range).west(range).below(range)))){
                if(e.equals(livingEntity) || !(e instanceof LivingEntity))
                    continue;
                dealDamage(world, shooter, damage, spellStats, e, source);
                e.setSecondsOnFire(fireSec);
                vec = e.position();
                ((ServerLevel)world).sendParticles(ParticleTypes.FLAME, vec.x, vec.y +0.5, vec.z,50,
                        ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1),ParticleUtil.inRange(-0.1, 0.1), 0.3);
            }
        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 7.0);
        addAmpConfig(builder, 3.0);
        addExtendTimeConfig(builder, 1);
    }

    @Override
    public int getManaCost() {
        return 40;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE,
                AugmentAOE.INSTANCE,
                AugmentFortune.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "When used on entities that are on fire, Flare causes a burst of damage and will spread fire and deal damage to other nearby entities. Does significantly more damage than Harm. Can be augmented with Extend Time, Amplify, and AOE.";
    }

    @Override
    public Item getCraftingReagent() {
        return ArsNouveauAPI.getInstance().getGlyphItem(EffectIgnite.INSTANCE);
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_FIRE);
    }
}
