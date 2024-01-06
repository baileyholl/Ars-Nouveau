package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.common.entity.Cinder;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.setup.registry.DamageTypesRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectFlare extends AbstractEffect implements IDamageEffect {
    public static EffectFlare INSTANCE = new EffectFlare();

    private EffectFlare() {
        super(GlyphLib.EffectFlareID, "Flare");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!(rayTraceResult.getEntity() instanceof LivingEntity livingEntity))
            return;
        Vec3 vec = safelyGetHitPos(rayTraceResult);
        float damage = (float) (DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier());
        double range = 3 + spellStats.getAoeMultiplier();
        int fireSec = (int) (5.0 + EXTEND_TIME.get() * spellStats.getDurationMultiplier());
        DamageSource source = buildDamageSource(world, shooter);
        world.addFreshEntity(new Cinder(world, vec.x, vec.y, vec.z));
//        if (livingEntity.isOnFire() && attemptDamage(world, shooter, spellStats, spellContext, resolver, livingEntity, source, damage)) {
//            ((ServerLevel) world).sendParticles(ParticleTypes.FLAME, vec.x, vec.y + 0.5, vec.z, 50,
//                    ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), 0.3);
//            for (Entity e : world.getEntities(shooter, new AABB(
//                    livingEntity.position().add(range, range, range), livingEntity.position().subtract(range, range, range)))) {
//                if (e.equals(livingEntity) || !(e instanceof LivingEntity))
//                    continue;
//                attemptDamage(world, shooter, spellStats, spellContext, resolver, e, source, damage);
//                e.setSecondsOnFire(fireSec);
//                vec = e.position();
//                ((ServerLevel) world).sendParticles(ParticleTypes.FLAME, vec.x, vec.y + 0.5, vec.z, 50,
//                        ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), 0.3);
//
//            }
//        }
    }


    @Override
    public DamageSource buildDamageSource(Level world, LivingEntity shooter) {
        return DamageUtil.source(world, DamageTypesRegistry.FLARE, shooter);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 7.0);
        addAmpConfig(builder, 3.0);
        addExtendTimeConfig(builder, 1);
    }

    @Override
    public int getDefaultManaCost() {
        return 40;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE,
                AugmentAOE.INSTANCE,
                AugmentFortune.INSTANCE, AugmentRandomize.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "When used on entities that are on fire, Flare causes a burst of damage and will spread fire and deal damage to other nearby entities. Does significantly more damage than Harm. Can be augmented with Extend Time, Amplify, and AOE.";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_FIRE);
    }
}
