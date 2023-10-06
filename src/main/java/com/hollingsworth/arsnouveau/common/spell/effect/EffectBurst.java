package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Predicate;

public class EffectBurst extends AbstractEffect {

    //TODO use GlyphLib#EffectBurstId
    public static final EffectBurst INSTANCE = new EffectBurst("burst", "Burst");

    public EffectBurst(String tag, String description) {
        super(tag, description);
    }

    @Override
    public String getBookDescription() {
        return "Resolves the spell in a spherical area around the target. Augment with Sensitive to target blocks instead of entities and Dampen to make an empty sphere. Augment with AOE to increase the radius. ";
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
        Predicate<Double> Sphere = spellStats.hasBuff(AugmentDampen.INSTANCE) ? (distance) -> distance <= radius + 0.5 && distance >= radius - 0.5 : (distance) -> (distance <= radius + 0.5);
        if (spellStats.isSensitive()) {
            //TODO check if BlockPos.betweenClosed is better
            for (BlockPos pos : BlockPos.withinManhattan(center, radius, radius, radius)) {
                if (Sphere.test(BlockUtil.distanceFromCenter(pos, center))) {
                    //TODO it needs a direction, UP as a dummy for now
                    resolver.getNewResolver(newContext.clone()).onResolveEffect(world, new BlockHitResult(new Vec3(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false));
                }
            }
        } else {
            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, new AABB(center).inflate(radius, radius, radius))) {
                if (Sphere.test(BlockUtil.distanceFromCenter(entity.blockPosition(), center))) {
                    resolver.getNewResolver(newContext.clone()).onResolveEffect(world, new EntityHitResult(entity));
                }
            }
        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", 1, 1, 1);
    }

    @Override
    public int getDefaultManaCost() {
        return 500;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentSensitive.INSTANCE, AugmentDampen.INSTANCE);
    }

    @Override
    protected void addDefaultInvalidCombos(Set<ResourceLocation> defaults) {
        defaults.add(EffectLinger.INSTANCE.getRegistryName());
        defaults.add(EffectWall.INSTANCE.getRegistryName());
    }
}
