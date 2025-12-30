package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
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
import java.util.Map;
import java.util.Set;

public class EffectRotate extends AbstractEffect {

    public static EffectRotate INSTANCE = new EffectRotate();

    public EffectRotate() {
        super(GlyphLib.EffectRotateID, "Rotate");
    }

    @Override
    public String getBookDescription() {
        return "Rotates a block or an entity clockwise. If augmented with sensitive it will change the axis of the block (if possible) or force the entity to turn their look. Dampen will rotate counter-clockwise.";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        boolean sensitive = spellStats.isSensitive();
        boolean randomize = spellStats.isRandomized();
        int ampMod = (int) spellStats.getAmpMultiplier();
        boolean counterClockwise = ampMod < 0;
        for (int i = 0; i < 1 + Math.abs(ampMod); i++) {
            float angle;
            if (randomize) {
                angle = world.random.nextFloat() * 360;
            } else {
                angle = entity.rotate(counterClockwise ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90);
            }

            if (sensitive) {
                entity.lookAt(EntityAnchorArgument.Anchor.FEET, entity.position.add(entity.getLookAngle().yRot(angle)));
            } else {
                entity.setYRot(angle);
            }
            if (entity instanceof Projectile projectile) {
                Vec3 vec3d = projectile.getDeltaMovement();
                projectile.setDeltaMovement(rotateVec(vec3d, randomize ? angle : counterClockwise ? -90 : 90));
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
        boolean swapAxis = spellStats.isSensitive();
        boolean randomize = spellStats.isRandomized();
        for (BlockPos pos : posList) {
            BlockState state = world.getBlockState(pos);

            if (randomize) {
                if (state.hasProperty(BlockStateProperties.AXIS)) {
                    state = state.setValue(BlockStateProperties.AXIS, Axis.getRandom(world.random));
                } else if (state.hasProperty(BlockStateProperties.FACING)) {
                    state = state.setValue(BlockStateProperties.FACING, Direction.getRandom(world.random));
                }

                state = state.rotate(world, pos, Rotation.getRandom(world.random));
            } else {
                int ampMod = (int) spellStats.getAmpMultiplier();
                boolean counterClockwise = ampMod < 0;

                for (int i = 0; i < (counterClockwise ? 0 : 1) + Math.abs(ampMod); i++) {
                    if (swapAxis) {
                        if (state.hasProperty(BlockStateProperties.AXIS)) {
                            state = state.setValue(BlockStateProperties.AXIS, switch (state.getValue(BlockStateProperties.AXIS)) {
                                case X -> counterClockwise ? Axis.Z : Axis.Y;
                                case Y -> counterClockwise ? Axis.X : Axis.Z;
                                case Z -> counterClockwise ? Axis.Y : Axis.X;
                            });
                        } else if (state.hasProperty(BlockStateProperties.FACING)) {
                            Direction curr = state.getValue(BlockStateProperties.FACING);
                            state = state.setValue(BlockStateProperties.FACING, switch (curr) {
                                case DOWN -> counterClockwise ? Direction.SOUTH : Direction.NORTH;
                                case UP -> counterClockwise ? Direction.NORTH : Direction.SOUTH;
                                case NORTH, EAST -> counterClockwise ? Direction.DOWN : Direction.UP;
                                case SOUTH, WEST -> counterClockwise ? Direction.UP : Direction.DOWN;
                            });
                        }
                    } else {
                        state = state.rotate(world, pos, counterClockwise ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90);
                    }
                }
            }

            world.setBlockAndUpdate(pos, state);
            ShapersFocus.tryPropagateBlockSpell(rayTraceResult, world, shooter, spellContext, resolver);
        }
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        super.addDefaultAugmentLimits(defaults);
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 1);
        defaults.put(AugmentRandomize.INSTANCE.getRegistryName(), 1);
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
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentSensitive.INSTANCE, AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentDampen.INSTANCE, AugmentRandomize.INSTANCE);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addBlockAoeAugmentDescriptions(map);
        map.put(AugmentSensitive.INSTANCE, "Rotates the block on a different axis or forces an entity to rotate their head.");
        map.put(AugmentDampen.INSTANCE, "Increases rotations counter-clockwise.");
        map.put(AugmentAmplify.INSTANCE, "Increases rotations clockwise.");
        map.put(AugmentRandomize.INSTANCE, "Applies a random rotation, ignoring axis.");
    }
}
