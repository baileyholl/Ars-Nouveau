package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class EffectKnockback extends AbstractEffect {
    public static EffectKnockback INSTANCE = new EffectKnockback();

    private EffectKnockback() {
        super(GlyphLib.EffectKnockbackID, "Knockback");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {

        float strength = (float) (GENERIC_DOUBLE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier());
        knockback(rayTraceResult.getEntity(), shooter, strength);
        rayTraceResult.getEntity().hurtMarked = true;

    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (spellStats.isSensitive()) return;
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats);
        float strength = (float) (GENERIC_DOUBLE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier());

        for (BlockPos p : posList) {
            EnchantedFallingBlock fallingBlock = EnchantedFallingBlock.fall(world, p, shooter, spellContext, resolver, spellStats);
            if (fallingBlock != null) {
                knockback(fallingBlock, shooter, strength);
                ShapersFocus.tryPropagateEntitySpell(fallingBlock, world, shooter, spellContext, resolver);
            }
        }
    }

    public void knockback(Entity target, LivingEntity shooter, float strength) {
        knockback(target, strength, Mth.sin(shooter.yRot * ((float) Math.PI / 180F)), -Mth.cos(shooter.yRot * ((float) Math.PI / 180F)));
    }

    public void knockback(Entity entity, double strength, double xRatio, double zRatio) {
        if (entity instanceof LivingEntity living)
            strength *= 1.0D - living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        if (strength > 0.0D) {
            entity.hasImpulse = true;
            Vec3 vec3 = entity.getDeltaMovement();
            Vec3 vec31 = (new Vec3(xRatio, 0.0D, zRatio)).normalize().scale(strength);
            entity.setDeltaMovement(vec3.x / 2.0D - vec31.x, entity.onGround() ? Math.min(0.4D, vec3.y / 2.0D + strength) : vec3.y, vec3.z / 2.0D - vec31.z);
        }
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericDouble(builder, 1.5, "Base knockback value", "base_value");
        addAmpConfig(builder, 1);
    }

    @Override
    public int getDefaultManaCost() {
        return 15;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentSensitive.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Knocks a target or block away a short distance from the caster. Sensitive will stop this spell from launching blocks.";
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_AIR);
    }
}
