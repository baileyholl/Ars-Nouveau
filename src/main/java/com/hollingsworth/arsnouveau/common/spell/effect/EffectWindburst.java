package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.WindCharge;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class EffectWindburst extends AbstractEffect {

    public static EffectWindburst INSTANCE = new EffectWindburst();

    public EffectWindburst() {
        super(GlyphLib.EffectWindburstID, "Wind Burst");
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);

        var loc = rayTraceResult.getLocation();
        double x = loc.x;
        double y = loc.y;
        double z = loc.z;

        var dummyWindCharge = new WindCharge(world, x, y, z, Vec3.ZERO);
        if (spellStats.isSensitive()) {
            dummyWindCharge.setOwner(shooter);
        }
        // 1.21.11: the 11-param explode with SimpleExplosionDamageCalculator was removed.
        // Use the WindCharge entity's built-in explosion logic via a direct explode call.
        float radius = 1.2f + (float) (spellStats.getAoeMultiplier() * this.GENERIC_DOUBLE.getAsDouble());
        world.explode(
                spellStats.isSensitive() ? shooter : dummyWindCharge,
                x,
                y,
                z,
                radius,
                false,
                Level.ExplosionInteraction.TRIGGER
        );
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addAmpConfig(builder, 0.25f);
        addGenericDouble(builder, 1.0f, "AOE Multiplier", "aoe_multiplier");
    }

    @Override
    public String getBookDescription() {
        return "Activates a wind charge at the target location, knocking entities back. Sensitive will allow the caster to ignore the knockback.";
    }

    @Override
    protected void addDefaultAugmentLimits(Map<Identifier, Integer> defaults) {
        super.addDefaultAugmentLimits(defaults);
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 1);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentSensitive.INSTANCE, "Caster is not affected by knockback");
        map.put(AugmentAOE.INSTANCE, "Increases the area of effect");
        map.put(AugmentAmplify.INSTANCE, "Increases the knockback strength");
        map.put(AugmentDampen.INSTANCE, "Decreases the knockback strength");
    }

    @Override
    protected int getDefaultManaCost() {
        return 30;
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_AIR);
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Set.of(AugmentAOE.INSTANCE, AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentSensitive.INSTANCE);
    }
}
