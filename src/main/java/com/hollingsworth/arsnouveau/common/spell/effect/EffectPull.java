package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectPull extends AbstractEffect {
    public static EffectPull INSTANCE = new EffectPull();

    private EffectPull() {
        super(GlyphLib.EffectPullID, "Pull");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity target = rayTraceResult.getEntity();
        Vec3 vec3d = new Vec3(shooter.getX() - target.getX(), shooter.getY() - target.getY(), shooter.getZ() - target.getZ());
        double d2 = GENERIC_DOUBLE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier();
        target.setDeltaMovement(vec3d.normalize().scale(d2));
        target.hurtMarked = true;
    }

    @Override
    public void onResolveBlock(BlockHitResult blockHitResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, blockHitResult.getBlockPos(), blockHitResult, spellStats);
        for (BlockPos p : posList) {
            if (!canBlockBeHarvested(spellStats, world, p)) {
                continue;
            }
            EnchantedFallingBlock fallingblockentity = EnchantedFallingBlock.fall(world, p, shooter, spellContext, resolver, spellStats);
            if (fallingblockentity != null) {
                setMotion(fallingblockentity, blockHitResult, spellStats);
                ShapersFocus.tryPropagateEntitySpell(fallingblockentity, world, shooter, spellContext, resolver);
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
    public void buildConfig(ForgeConfigSpec.Builder builder) {
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
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentAOE.INSTANCE, AugmentPierce.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Pulls the target closer to the caster. When used on blocks, they become falling blocks with motion towards the side of the block that was hit.";
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_AIR);
    }
}
