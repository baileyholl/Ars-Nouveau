package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectPull extends AbstractEffect {
    public static EffectPull INSTANCE = new EffectPull();

    private EffectPull() {
        super(GlyphLib.EffectPullID, "Pull");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity target = rayTraceResult.getEntity();
        Vec3 vec3d = new Vec3(shooter.getX() - target.getX(), shooter.getY() - target.getY(), shooter.getZ() - target.getZ());
        double d2 = GENERIC_DOUBLE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier();
        target.setDeltaMovement(target.getDeltaMovement().add(vec3d.normalize().scale(d2 )));
        target.hurtMarked = true;
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        if(rayTraceResult instanceof BlockHitResult blockHitResult){
            List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, blockHitResult.getBlockPos(), blockHitResult, spellStats);
            EnchantedFallingBlock newTarget = null;
            for(BlockPos p : posList) {
                BlockState state = world.getBlockState(p);
                if(!canBlockBeHarvested(spellStats, world, p)) {
                    continue;
                }
                EnchantedFallingBlock fallingblockentity = new EnchantedFallingBlock(world,
                        p.getX() + 0.5D,
                        p.getY(),
                        p.getZ() + 0.5D,
                        state.hasProperty(BlockStateProperties.WATERLOGGED) ? state.setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE) : state);
                double scalar = 0.5 + ParticleUtil.inRange(-0.1, 0.1);
                Vec3i directionVec = blockHitResult.getDirection().getNormal();
                Vec3 deltaVec = new Vec3(directionVec.getX() * scalar, directionVec.getY() * scalar + 0.1, directionVec.getZ() * scalar);
                fallingblockentity.setDeltaMovement(deltaVec);
                world.setBlock(p, state.getFluidState().createLegacyBlock(), 3);
                world.addFreshEntity(fallingblockentity);
                if(p.equals(blockHitResult.getBlockPos())) {
                    newTarget = fallingblockentity;
                }
            }
            //CuriosUtil.hasItem(shooter, ItemsRegistry.SHAPERS_FOCUS) &&
            if(newTarget != null){
                resolver.hitResult = new EntityHitResult(newTarget, newTarget.position);
            }
        }
    }

    //    private boolean moveBlocks(Level pLevel, BlockPos pPos, Direction pDirection, boolean pExtending) {
//        BlockPos blockpos = pPos.relative(pDirection);
//        if (!pExtending && pLevel.getBlockState(blockpos).is(Blocks.PISTON_HEAD)) {
//            pLevel.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 20);
//        }
//
//        PistonStructureResolver pistonstructureresolver = new PistonStructureResolver(pLevel, pPos, pDirection, pExtending);
//        if (!pistonstructureresolver.resolve()) {
//            return false;
//        } else {
//            Map<BlockPos, BlockState> map = Maps.newHashMap();
//            List<BlockPos> list = pistonstructureresolver.getToPush();
//            List<BlockState> list1 = Lists.newArrayList();
//
//            for(int i = 0; i < list.size(); ++i) {
//                BlockPos blockpos1 = list.get(i);
//                BlockState blockstate = pLevel.getBlockState(blockpos1);
//                list1.add(blockstate);
//                map.put(blockpos1, blockstate);
//            }
//
//            List<BlockPos> list2 = pistonstructureresolver.getToDestroy();
//            BlockState[] ablockstate = new BlockState[list.size() + list2.size()];
//            Direction direction = pExtending ? pDirection : pDirection.getOpposite();
//            int j = 0;
//
//            for(int k = list2.size() - 1; k >= 0; --k) {
//                BlockPos blockpos2 = list2.get(k);
//                BlockState blockstate1 = pLevel.getBlockState(blockpos2);
//                BlockEntity blockentity = blockstate1.hasBlockEntity() ? pLevel.getBlockEntity(blockpos2) : null;
//                dropResources(blockstate1, pLevel, blockpos2, blockentity);
//                pLevel.setBlock(blockpos2, Blocks.AIR.defaultBlockState(), 18);
//                if (!blockstate1.is(BlockTags.FIRE)) {
//                    pLevel.addDestroyBlockEffect(blockpos2, blockstate1);
//                }
//
//                ablockstate[j++] = blockstate1;
//            }
//
//            for(int l = list.size() - 1; l >= 0; --l) {
//                BlockPos blockpos3 = list.get(l);
//                BlockState blockstate5 = pLevel.getBlockState(blockpos3);
//                blockpos3 = blockpos3.relative(direction);
//                map.remove(blockpos3);
//                BlockState blockstate8 = Blocks.MOVING_PISTON.defaultBlockState().setValue(FACING, pDirection);
//                pLevel.setBlock(blockpos3, blockstate8, 68);
//                pLevel.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(blockpos3, blockstate8, list1.get(l), pDirection, pExtending, false));
//                ablockstate[j++] = blockstate5;
//            }
//
//            if (pExtending) {
////                PistonType pistontype = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
////                BlockState blockstate4 = Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, pDirection).setValue(PistonHeadBlock.TYPE, pistontype);
////                BlockState blockstate6 = Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, pDirection).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
////                map.remove(blockpos);
////                pLevel.setBlock(blockpos, blockstate6, 68);
////                pLevel.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(blockpos, blockstate6, blockstate4, pDirection, true, true));
//            }
//
//            BlockState blockstate3 = Blocks.AIR.defaultBlockState();
//
//            for(BlockPos blockpos4 : map.keySet()) {
//                pLevel.setBlock(blockpos4, blockstate3, 82);
//            }
//
//            for(Map.Entry<BlockPos, BlockState> entry : map.entrySet()) {
//                BlockPos blockpos5 = entry.getKey();
//                BlockState blockstate2 = entry.getValue();
//                blockstate2.updateIndirectNeighbourShapes(pLevel, blockpos5, 2);
//                blockstate3.updateNeighbourShapes(pLevel, blockpos5, 2);
//                blockstate3.updateIndirectNeighbourShapes(pLevel, blockpos5, 2);
//            }
//
//            j = 0;
//
//            for(int i1 = list2.size() - 1; i1 >= 0; --i1) {
//                BlockState blockstate7 = ablockstate[j++];
//                BlockPos blockpos6 = list2.get(i1);
//                blockstate7.updateIndirectNeighbourShapes(pLevel, blockpos6, 2);
//                pLevel.updateNeighborsAt(blockpos6, blockstate7.getBlock());
//            }
//
//            for(int j1 = list.size() - 1; j1 >= 0; --j1) {
//                pLevel.updateNeighborsAt(list.get(j1), ablockstate[j++].getBlock());
//            }
//
//            if (pExtending) {
//                pLevel.updateNeighborsAt(blockpos, Blocks.PISTON_HEAD);
//            }
//
//            return true;
//        }
//    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericDouble(builder, 1.0, "Base movement velocity", "base_value");
        addAmpConfig(builder, 0.5);
    }

    @Override
    public boolean wouldSucceed(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        return rayTraceResult instanceof EntityHitResult;
    }

    @Override
    public int getDefaultManaCost() {
        return 15;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentAOE.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Pulls the target closer to the caster";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_AIR);
    }
}
