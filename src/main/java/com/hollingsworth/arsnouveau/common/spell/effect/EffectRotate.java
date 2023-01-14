package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class EffectRotate extends AbstractEffect {

    public static EffectRotate INSTANCE = new EffectRotate();

    public EffectRotate() {
        super(GlyphLib.EffectRotateID, "Rotate");
    }

    @Override
    public String getBookDescription() {
        return "Rotates a block or an entity clockwise. If augmented with sensitive it will change the axis of the block (if possible) or force the entity to turn their look";
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.ONE;
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        boolean sensitive = spellStats.getBuffCount(AugmentSensitive.INSTANCE) >= 1;

        for (int i = 0; i < 1 + spellStats.getAmpMultiplier(); i++) {
            float angle = entity.rotate(Rotation.CLOCKWISE_90);
            if (sensitive){
                entity.lookAt(EntityAnchorArgument.Anchor.FEET, entity.position.add(entity.getLookAngle().yRot(angle)));
            }else {
                entity.setYRot(angle);
            }
            if(entity instanceof Projectile projectile){
                Vec3 vec3d = projectile.getDeltaMovement();
                projectile.setDeltaMovement(rotateVec(vec3d, 90));
            }
            entity.hurtMarked = true;
        }
    }

    public Vec3 rotateVec(Vec3 vec, float angle) {
        // Rotate the vector around the Y axis
        double x = vec.x * Math.cos(angle) - vec.z * Math.sin(angle);
        double z = vec.x * Math.sin(angle) + vec.z * Math.cos(angle);
        return new Vec3(x, vec.y, z);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats);
        boolean swapAxis = spellStats.getBuffCount(AugmentSensitive.INSTANCE) >= 1;
        for (BlockPos pos : posList) {
            BlockState state = world.getBlockState(pos);
            for (int i = 0; i < 1 + spellStats.getAmpMultiplier(); i++) {
                if (swapAxis) {
                    if (state.hasProperty(BlockStateProperties.AXIS)) {
                        state = state.setValue(BlockStateProperties.AXIS, switch (state.getValue(BlockStateProperties.AXIS)) {
                            case X -> Axis.Y;
                            case Y -> Axis.Z;
                            case Z -> Axis.X;
                        });
                    } else if (state.hasProperty(BlockStateProperties.FACING)) {
                        Direction curr = state.getValue(BlockStateProperties.FACING);
                        state = state.setValue(BlockStateProperties.FACING, switch (curr) {
                            case DOWN -> Direction.NORTH;
                            case UP -> Direction.SOUTH;
                            case NORTH, EAST -> Direction.UP;
                            case SOUTH, WEST -> Direction.DOWN;
                        });
                    }
                } else {
                    state = state.rotate(world, pos, Rotation.CLOCKWISE_90);
                }
            }
            world.setBlockAndUpdate(pos, state);
            ShapersFocus.tryPropagateBlockSpell(rayTraceResult, world, shooter, spellContext, resolver);
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentSensitive.INSTANCE, AugmentAOE.INSTANCE, AugmentPierce.INSTANCE);
    }

}
