package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.Cinder;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.potions.BlastEffect;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DamageTypesRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;
import java.util.Set;

public class EffectFlare extends AbstractEffect implements IDamageEffect {
    public static EffectFlare INSTANCE = new EffectFlare();

    private EffectFlare() {
        super(GlyphLib.EffectFlareID, "Flare");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!(rayTraceResult.getEntity() instanceof LivingEntity livingEntity && world instanceof ServerLevel level))
            return;
        Vec3 vec = safelyGetHitPos(rayTraceResult);
        float damage = (float) (DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier());

        if (!canDamage(livingEntity))
            return;
        this.damage(vec, level, shooter, livingEntity, spellStats, spellContext, resolver, damage);
        spawnCinders(shooter, level,rayTraceResult.getLocation().add(0, (rayTraceResult.getEntity().onGround() ? 1 : 0),0), spellStats, spellContext, resolver);

        if(rayTraceResult.getEntity() instanceof LivingEntity living && living.hasEffect(ModPotions.BLAST_EFFECT)){
            int amplifier = living.getEffect(ModPotions.BLAST_EFFECT).getAmplifier();
            living.removeEffect(ModPotions.BLAST_EFFECT);
            BlastEffect.explode(living, amplifier + 1);
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolveBlock(rayTraceResult, world, shooter, spellStats, spellContext, resolver);

        if(world.getBlockState(rayTraceResult.getBlockPos()).is(BlockTags.FIRE)){
            spawnCinders(shooter, world, rayTraceResult.getLocation(), spellStats, spellContext, resolver);
            return;
        }

        for(Direction d : Direction.values()){
            if(world.getBlockState(rayTraceResult.getBlockPos().relative(d)).is(BlockTags.FIRE)){
                spawnCinders(shooter, world, rayTraceResult.getLocation(), spellStats, spellContext, resolver);
                return;
            }
        }
    }

    public boolean canDamage(LivingEntity livingEntity) {
        return livingEntity.isOnFire() || livingEntity.hasEffect(ModPotions.BLAST_EFFECT) || livingEntity.level.getBlockState(livingEntity.blockPosition()).is(BlockTags.FIRE);
    }

    public void spawnCinders(LivingEntity shooter, Level level, Vec3 hit, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver){
        double radiusMultiplier = 1;
        int max = (int) Math.ceil(3 + spellStats.getAoeMultiplier());
        Random random = new Random();
        for(int i = 0; i < max; i++) {
            float offset = i * 15;
            Vec3 vec3 = new Vec3(
                    hit.x() - radiusMultiplier * Math.sin(random.nextInt(360)),
                    hit.y(), // Offset if the owner died
                    hit.z() - radiusMultiplier * Math.cos(random.nextInt(360)));
            Vec3 scaleVec =  new Vec3(ParticleUtil.inRange(0.1, 0.5), 1, ParticleUtil.inRange(0.1, 0.5));

            Cinder fallingBlock = new Cinder(level, vec3.x(), vec3.y(), vec3.z(), BlockRegistry.MAGIC_FIRE.defaultBlockState(), resolver);
            // Send the falling block the opposite direction of the target
            fallingBlock.setDeltaMovement(vec3.x() - hit.x(), ParticleUtil.inRange(0.1, 0.5), vec3.z() - hit.z());
            fallingBlock.setDeltaMovement(fallingBlock.getDeltaMovement().multiply(scaleVec));
            fallingBlock.dropItem = false;
            fallingBlock.hurtEntities = true;
            fallingBlock.baseDamage = ((float) (DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier())) * 0.5f;
            fallingBlock.shooter = shooter;
            level.addFreshEntity(fallingBlock);
            ShapersFocus.tryPropagateEntitySpell(fallingBlock, level, shooter, spellContext, resolver);
        }
    }



    public void damage(Vec3 vec, ServerLevel world, LivingEntity shooter, LivingEntity livingEntity, SpellStats stats, SpellContext context, SpellResolver resolver, float damage) {
        if (attemptDamage(world, shooter, stats, context, resolver, livingEntity, buildDamageSource(world, shooter), damage)) {
            world.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, vec.x, vec.y + 0.5, vec.z, 50,
                    ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), 0.3);
        }
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
    public void buildConfig(ModConfigSpec.Builder builder) {
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
                AugmentAOE.INSTANCE,
                AugmentFortune.INSTANCE, AugmentRandomize.INSTANCE
        );
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentAOE.INSTANCE, "Increases the number of cinders spawned.");
    }

    @Override
    public String getBookDescription() {
        return "When used on an entity or block that is on fire or afflicted with Blasting, Cinders will explode around the target, dealing damage and creating Mage Fire nearby. Mage Fire cannot spread to blocks and is short lived. Entities with Blasting will immediately explode for extra strength.";
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
