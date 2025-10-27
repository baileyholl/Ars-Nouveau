package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EffectLaunch extends AbstractEffect {
    public static EffectLaunch INSTANCE = new EffectLaunch();

    private EffectLaunch() {
        super(GlyphLib.EffectLaunchID, "Launch");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        entity.setDeltaMovement(entity.getDeltaMovement().add(0, GENERIC_DOUBLE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier(), 0));
        entity.hurtMarked = true;
        entity.fallDistance = 0.0f;
    }

    @Override
    public void onResolveBlock(BlockHitResult result, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (spellStats.isSensitive()) return;
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, result.getBlockPos(), result, spellStats);
        for (BlockPos pos1 : posList) {
            EnchantedFallingBlock entity = EnchantedFallingBlock.fall(world, pos1, shooter, spellContext, resolver, spellStats);
            if (entity != null) {
                entity.setDeltaMovement(entity.getDeltaMovement().add(0, (GENERIC_DOUBLE.get() * .6) + AMP_VALUE.get() * spellStats.getAmpMultiplier(), 0));
                entity.hurtMarked = true;
                entity.fallDistance = 0.0f;
                ShapersFocus.tryPropagateEntitySpell(entity, world, shooter, spellContext, resolver);
            }
        }
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericDouble(builder, 0.8, "Base knockup amount", "knockup");
        addAmpConfig(builder, 0.25);
    }

    @Override
    public int getDefaultManaCost() {
        return 30;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentAOE.INSTANCE, AugmentSensitive.INSTANCE, AugmentPierce.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Launches an entity or block into the air. Can be used for large jumps, yeeting mobs in the sky or for scaling mountains! Sensitive will stop this spell from launching blocks.";
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        super.addDefaultAugmentLimits(defaults);
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 1);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addBlockAoeAugmentDescriptions(map);
        map.put(AugmentAmplify.INSTANCE, "Increases the knockup amount.");
        map.put(AugmentDampen.INSTANCE, "Decreases the knockup amount.");
        map.put(AugmentSensitive.INSTANCE, "Prevents blocks from being moved.");
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_AIR);
    }
}
