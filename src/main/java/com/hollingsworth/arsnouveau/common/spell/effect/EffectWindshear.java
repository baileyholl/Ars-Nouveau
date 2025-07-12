package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentRandomize;
import com.hollingsworth.arsnouveau.setup.registry.DamageTypesRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectWindshear extends AbstractEffect implements IDamageEffect {

    public static EffectWindshear INSTANCE = new EffectWindshear();

    private EffectWindshear() {
        super(GlyphLib.EffectWindshearID, "Wind Shear");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!rayTraceResult.getEntity().onGround() && !(rayTraceResult.getEntity() instanceof ItemEntity)) {
            int numBlocks = 0;
            BlockPos pos = rayTraceResult.getEntity().blockPosition();
            while (!world.getBlockState(pos.below()).blocksMotion() && numBlocks <= 10) {
                pos = pos.below();
                numBlocks++;
            }

            float damage = (float) (DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier() + numBlocks);

            if (attemptDamage(world, shooter, spellStats, spellContext, resolver, rayTraceResult.getEntity(), buildDamageSource(world, shooter), damage)) {//converted DamageSource FALL into playerAttack
                Vec3 vec = rayTraceResult.getEntity().position;
                for (int i = 0; i < 10; i++) {
                    ((ServerLevel) world).sendParticles(ParticleTypes.SWEEP_ATTACK, vec.x + ParticleUtil.inRange(-0.2, 0.2), vec.y + 0.5 + ParticleUtil.inRange(-0.2, 0.2), vec.z + ParticleUtil.inRange(-0.2, 0.2), 30,
                            ParticleUtil.inRange(-0.2, 0.2), ParticleUtil.inRange(-0.2, 0.2), ParticleUtil.inRange(-0.2, 0.2), 0.3);
                }
            }
        }
    }

    @Override
    public DamageSource buildDamageSource(Level world, LivingEntity shooter) {
        return DamageUtil.source(world, DamageTypesRegistry.WINDSHEAR, shooter);
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 5);
        addAmpConfig(builder, 2.5f);
        addGenericDouble(builder, 0.75, "Damage per block in the air", "airDamage");
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    @Override
    public String getBookDescription() {
        return "Deals damage to targets in the air, with an increasing amount based on how high the target is off the ground, up to 10 blocks. Targets on the ground take no damage.";
    }

    @Override
    public int getDefaultManaCost() {
        return 50;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_AIR);
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentDampen.INSTANCE, AugmentAmplify.INSTANCE, AugmentRandomize.INSTANCE);
    }
}
