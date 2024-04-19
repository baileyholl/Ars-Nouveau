package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class EffectLeap extends AbstractEffect {
    public static EffectLeap INSTANCE = new EffectLeap();

    private EffectLeap() {
        super(GlyphLib.EffectLeapID, "Leap");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        double bonus;
        Vec3 vector;
        if (NERF.get() && entity == shooter && !shooter.onGround()) return;
        if (entity instanceof LivingEntity) {
            vector = entity.getLookAngle();
            bonus = Math.max(0, GENERIC_DOUBLE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier());
        } else {
            vector = shooter.getLookAngle();
            if (spellContext.getCaster() instanceof TileCaster tc) {
                BlockEntity tile = tc.getTile();
                if (tile instanceof RotatingTurretTile rotatingTurretTile) {
                    vector = rotatingTurretTile.getShootAngle();
                } else if (tile instanceof BasicSpellTurretTile || tile instanceof RuneTile) {
                    vector = new Vec3(tile.getBlockState().getValue(FACING).step());
                }
            }
            bonus = GENERIC_DOUBLE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier();
        }
        if (spellStats.hasBuff(AugmentAccelerate.INSTANCE))
            entity.setDeltaMovement(entity.getDeltaMovement().add(vector.x * bonus, vector.y * bonus, vector.z * bonus));
        else entity.setDeltaMovement(vector.x * bonus, vector.y * bonus, vector.z * bonus);

        entity.fallDistance = 0.0f;
        entity.hurtMarked = true;
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats);
        for (BlockPos pos1 : posList) {
            EnchantedFallingBlock entity = EnchantedFallingBlock.fall(world, pos1, shooter, spellContext, resolver, spellStats);
            if (entity != null) {
                Vec3 vector = shooter.getLookAngle();
                double bonus = GENERIC_DOUBLE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier();
                entity.setDeltaMovement(vector.x * bonus, vector.y * bonus, vector.z * bonus);
                entity.hurtMarked = true;
                entity.fallDistance = 0.0f;
                ShapersFocus.tryPropagateEntitySpell(entity, world, shooter, spellContext, resolver);
            }
        }
    }

    ForgeConfigSpec.BooleanValue NERF;

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        NERF = builder.comment("If true, will not launch the caster if they are not on the ground.").define("force_ground", false);
        addGenericDouble(builder, 1.5, "Base knockup amount", "knock_up");
        addAmpConfig(builder, 1.0);
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentAccelerate.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Launches the target in the direction they are looking. Amplification will increase the distance moved. With Accelerate the momentum will be kept instead of overriden.";
    }

    @Override
    public int getDefaultManaCost() {
        return 25;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_AIR);
    }
}
