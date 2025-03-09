package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.BlockPos;
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

public class EffectGravity extends AbstractEffect implements IPotionEffect {
    public static EffectGravity INSTANCE = new EffectGravity();

    private EffectGravity() {
        super(GlyphLib.EffectGravityID, "Gravity");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getBlockPos();
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, spellStats.getAoeMultiplier(), spellStats.getBuffCount(AugmentPierce.INSTANCE));

        for (BlockPos pos1 : posList) {
            EnchantedFallingBlock fallingBlock = EnchantedFallingBlock.fall(world, pos1, shooter, spellContext, resolver, spellStats);
            if (fallingBlock != null)
                ShapersFocus.tryPropagateEntitySpell(fallingBlock, world, shooter, spellContext, resolver);
        }
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof LivingEntity living) {
            if (spellStats.hasBuff(AugmentExtendTime.INSTANCE)) {
                this.applyConfigPotion(living, ModPotions.GRAVITY_EFFECT, spellStats);
            } else {
                Entity entity = rayTraceResult.getEntity();
                entity.setDeltaMovement(entity.getDeltaMovement().add(0, -1.0 - spellStats.getAmpMultiplier(), 0));
                entity.hurtMarked = true;
            }
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 15;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 30);
        addExtendTimeConfig(builder, 8);
    }

   @NotNull
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

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addBlockAoeAugmentDescriptions(map);
        map.put(AugmentDampen.INSTANCE, "Reduces the falling speed of blocks and entities, or decreases the effect of the potion.");
        map.put(AugmentAmplify.INSTANCE, "Increases the falling speed of blocks and entities, or increases the effect of the potion.");
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_AIR);
    }

    @Override
    public int getBaseDuration() {
        return POTION_TIME == null ? 30 : POTION_TIME.get();
    }

    @Override
    public int getExtendTimeDuration() {
        return EXTEND_TIME == null ? 8 : EXTEND_TIME.get();
    }
}
