package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EffectPull extends AbstractEffect {
    public static EffectPull INSTANCE = new EffectPull();

    private EffectPull() {
        super(GlyphLib.EffectPullID, "Pull");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity target = rayTraceResult.getEntity();
        Vec3 vec3d = new Vec3(shooter.getX() - target.getX(), shooter.getY() - target.getY(), shooter.getZ() - target.getZ());
        double d2 = GENERIC_DOUBLE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier();
        target.setDeltaMovement(vec3d.normalize().scale(d2));
        target.hurtMarked = true;
    }

    @Override
    public void onResolveBlock(BlockHitResult blockHitResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (spellStats.isSensitive()) return;
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, blockHitResult.getBlockPos(), blockHitResult, spellStats);
        for (BlockPos p : posList) {
            if (!canBlockBeHarvested(spellStats, world, p)) {
                continue;
            }
            EnchantedFallingBlock fallingBlockEntity = EnchantedFallingBlock.fall(world, p, shooter, spellContext, resolver, spellStats);
            if (fallingBlockEntity != null) {
                setMotion(fallingBlockEntity, blockHitResult, spellStats);
                ShapersFocus.tryPropagateEntitySpell(fallingBlockEntity, world, shooter, spellContext, resolver);
            }
        }
    }


    public void setMotion(Entity entity, BlockHitResult blockHitResult, SpellStats spellStats) {
        double scalar = 0.5 + ParticleUtil.inRange(-0.05, 0.05) + spellStats.getAmpMultiplier() * .3;
        Vec3i directionVec = blockHitResult.getDirection().getNormal();
        Vec3 deltaVec = new Vec3(directionVec.getX() * scalar, directionVec.getY() * scalar + 0.1, directionVec.getZ() * scalar);
        entity.setDeltaMovement(deltaVec);
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericDouble(builder, 1.0, "Base movement velocity", "base_value");
        addAmpConfig(builder, 0.5);
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
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        super.addDefaultAugmentLimits(defaults);
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 1);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addBlockAoeAugmentDescriptions(map);
        map.put(AugmentAmplify.INSTANCE, "Increases the velocity.");
        map.put(AugmentDampen.INSTANCE, "Decreases the velocity.");
        map.put(AugmentSensitive.INSTANCE, "Prevents blocks from being pulled.");
    }

    @Override
    public String getBookDescription() {
        return "Pulls the target closer to the caster. When used on blocks, they become falling blocks with motion towards the side of the block that was hit. Sensitive will stop this spell from pulling blocks.";
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_AIR);
    }
}
