package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SummonBed extends ModBlock {
    public static final VoxelShape collisionShape = Block.box(0, 0, 0, 16, 3, 16);
    public static final VoxelShape shape = Block.box(0, 0, 0, 16, 4, 16);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public SummonBed() {
        this(defaultProperties());
    }

    public SummonBed(Properties properties) {
        super(properties);
        registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.POWERED, false));
    }

    public boolean isPowered(BlockState state, Level level, BlockPos pos) {
        return state.getValue(POWERED);
    }

    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        boolean flag = pLevel.hasNeighborSignal(pPos);
        if (flag != pState.getValue(POWERED)) {
            pLevel.setBlock(pPos, pState.setValue(POWERED, flag), 2);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return collisionShape;
    }

    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return shape;
    }

    public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POWERED);
    }

    public void fallOn(Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity, float pFallDistance) {
        super.fallOn(pLevel, pState, pPos, pEntity, pFallDistance * 0.5F);
    }

    /**
     * Called when an Entity lands on this Block.
     * This method is responsible for doing any modification on the motion of the entity that should result from the
     * landing.
     */
    public void updateEntityAfterFallOn(BlockGetter pLevel, Entity pEntity) {
        if (pEntity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(pLevel, pEntity);
        } else {
            this.bounceUp(pEntity);
        }
    }

    private void bounceUp(Entity pEntity) {
        Vec3 vec3 = pEntity.getDeltaMovement();
        if (vec3.y < 0.0D) {
            double d0 = pEntity instanceof LivingEntity ? 1.0D : 0.8D;
            pEntity.setDeltaMovement(vec3.x, -vec3.y * (double) 0.66F * d0, vec3.z);
        }
    }

}
