package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectBurst extends AbstractEffect {

    public static final EffectBurst INSTANCE = new EffectBurst("burst", "Burst");

    public EffectBurst(String tag, String description) {
        super(tag, description);
        invalidCombinations.add(EffectLinger.INSTANCE.getRegistryName());
        invalidCombinations.add(EffectWall.INSTANCE.getRegistryName());
    }

    @Override //TODO
    public String getBookDescription() {
        return super.getBookDescription();
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        makeSphere(rayTraceResult.getBlockPos(), world, shooter, spellStats, spellContext, resolver);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        makeSphere(rayTraceResult.getEntity().blockPosition(), world, shooter, spellStats, spellContext, resolver);
    }

    public void makeSphere(BlockPos center, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver){
        spellContext.setCanceled(true);
        if (spellContext.getRemainingSpell().isEmpty()) return;
        SpellContext newContext = resolver.spellContext.clone().withSpell(spellContext.getRemainingSpell());

        int radius = (int) (1 + spellStats.getAoeMultiplier());
        if (spellStats.hasBuff(AugmentSensitive.INSTANCE)) {
            //TODO check if BlockPos.betweenClosed is better
            for (BlockPos pos : BlockPos.withinManhattan(center, radius, radius, radius)) {
                if (BlockUtil.distanceFromCenter(pos, center) <= radius + 0.5) {
                    //TODO it needs a direction, UP as a dummy for now
                    resolver.getNewResolver(newContext.clone()).onResolveEffect(world, new BlockHitResult(new Vec3(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false));
                }
            }
        } else {
            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, new AABB(center).inflate(radius, radius, radius))) {
                if (entity.distanceToSqr(Vec3.atCenterOf(center)) <= radius + 0.5) {
                    resolver.getNewResolver(newContext.clone()).onResolveEffect(world, new EntityHitResult(entity));
                }
            }
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 0;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentSensitive.INSTANCE);
    }

}
