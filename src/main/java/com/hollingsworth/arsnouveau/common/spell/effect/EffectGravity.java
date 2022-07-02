package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectGravity extends AbstractEffect {
    public static EffectGravity INSTANCE = new EffectGravity();

    private EffectGravity() {
        super(GlyphLib.EffectGravityID, "Gravity");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getBlockPos();
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, spellStats.getAoeMultiplier(), spellStats.getBuffCount(AugmentPierce.INSTANCE));

        for (BlockPos pos1 : posList) {
            if (world.getBlockEntity(pos1) != null || !canBlockBeHarvested(spellStats, world, pos1) || !BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, pos1))
                continue;
            EnchantedFallingBlock fallingBlock = EnchantedFallingBlock.fall(world, pos1, shooter, spellContext, resolver, spellStats);
            if (fallingBlock != null)
                ShapersFocus.tryPropagateEntitySpell(fallingBlock, world, shooter, spellContext, resolver);
        }
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof LivingEntity living) {
            if (spellStats.hasBuff(AugmentExtendTime.INSTANCE)) {
                applyConfigPotion(living, ModPotions.GRAVITY_EFFECT.get(), spellStats);
            } else {
                Entity entity = rayTraceResult.getEntity();
                entity.setDeltaMovement(entity.getDeltaMovement().add(0, -1.0 - spellStats.getDurationMultiplier(), 0));
                entity.hurtMarked = true;
            }
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 15;
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 30);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentAOE.INSTANCE,
                AugmentPierce.INSTANCE,
                AugmentExtendTime.INSTANCE,
                AugmentDurationDown.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Causes blocks and entities to fall. When augmented with Extend Time, players will have their flight disabled and will obtain the Gravity effect. While afflicted with Gravity, entities will rapidly fall and take double falling damage.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_AIR);
    }
}
