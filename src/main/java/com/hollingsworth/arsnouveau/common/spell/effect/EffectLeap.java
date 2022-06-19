package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectLeap extends AbstractEffect {
    public static EffectLeap INSTANCE = new EffectLeap();

    private EffectLeap() {
        super(GlyphLib.EffectLeapID, "Leap");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof LivingEntity e) {
            double bonus = GENERIC_DOUBLE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier();
            e.setDeltaMovement(e.getLookAngle().x * bonus, e.getLookAngle().y * bonus, e.getLookAngle().z * bonus);
            e.fallDistance = 0;
            e.hurtMarked = true;
        }
    }


    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericDouble(builder, 1.5, "Base knockup amount", "knock_up");
        addAmpConfig(builder, 1.0);
    }

    @Override
    public boolean wouldSucceed(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        return livingEntityHitSuccess(rayTraceResult);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Launches the target in the direction they are looking. Amplification will increase the distance moved.";
    }

    @Override
    public int getDefaultManaCost() {
        return 25;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_AIR);
    }
}
