package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ArchfruitPod extends HorizontalDirectionalBlock implements BonemealableBlock {
    public Supplier<Block> surviveBlock;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
    protected static final VoxelShape[] EAST_AABB = new VoxelShape[]{Block.box(11.0D, 7.0D, 6.0D, 15.0D, 12.0D, 10.0D), Block.box(9.0D, 5.0D, 5.0D, 15.0D, 12.0D, 11.0D), Block.box(7.0D, 3.0D, 4.0D, 15.0D, 12.0D, 12.0D)};
    protected static final VoxelShape[] WEST_AABB = new VoxelShape[]{Block.box(1.0D, 7.0D, 6.0D, 5.0D, 12.0D, 10.0D), Block.box(1.0D, 5.0D, 5.0D, 7.0D, 12.0D, 11.0D), Block.box(1.0D, 3.0D, 4.0D, 9.0D, 12.0D, 12.0D)};
    protected static final VoxelShape[] NORTH_AABB = new VoxelShape[]{Block.box(6.0D, 7.0D, 1.0D, 10.0D, 12.0D, 5.0D), Block.box(5.0D, 5.0D, 1.0D, 11.0D, 12.0D, 7.0D), Block.box(4.0D, 3.0D, 1.0D, 12.0D, 12.0D, 9.0D)};
    protected static final VoxelShape[] SOUTH_AABB = new VoxelShape[]{Block.box(6.0D, 7.0D, 11.0D, 10.0D, 12.0D, 15.0D), Block.box(5.0D, 5.0D, 9.0D, 11.0D, 12.0D, 15.0D), Block.box(4.0D, 3.0D, 7.0D, 12.0D, 12.0D, 15.0D)};

    public ArchfruitPod(Supplier<Block> surviveBlock) {
       this(BlockBehaviour.Properties.of(Material.PLANT).randomTicks().strength(0.2F, 3.0F).sound(SoundType.WOOD).noOcclusion());
       this.surviveBlock = surviveBlock;
    }

    public ArchfruitPod(BlockBehaviour.Properties p_51743_) {
        super(p_51743_);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(AGE, 0));
    }

    /**
     * @return whether this block needs random ticking.
     */
    public boolean isRandomlyTicking(BlockState pState) {
        return pState.getValue(AGE) < 2;
    }

    public void randomTick(BlockState p_221000_, ServerLevel p_221001_, BlockPos p_221002_, RandomSource p_221003_) {
        int i = p_221000_.getValue(AGE);
        if (i < 2 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_221001_, p_221002_, p_221000_, p_221001_.random.nextInt(5) == 0)) {
            p_221001_.setBlock(p_221002_, p_221000_.setValue(AGE, i + 1), 2);
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_221001_, p_221002_, p_221000_);
        }
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockState blockstate = pLevel.getBlockState(pPos.relative(pState.getValue(FACING)));
        return blockstate.getBlock() == this.surviveBlock.get();
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        int i = pState.getValue(AGE);
        switch (pState.getValue(FACING)) {
            case SOUTH:
                return SOUTH_AABB[i];
            case WEST:
                return WEST_AABB[i];
            case EAST:
                return EAST_AABB[i];
            case NORTH:
            default:
                return NORTH_AABB[i];
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockstate = this.defaultBlockState();
        LevelReader levelreader = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();

        for (Direction direction : pContext.getNearestLookingDirections()) {
            if (direction.getAxis().isHorizontal()) {
                blockstate = blockstate.setValue(FACING, direction);
                if (blockstate.canSurvive(levelreader, blockpos)) {
                    return blockstate;
                }
            }
        }

        return null;
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return pFacing == pState.getValue(FACING) && !pState.canSurvive(pLevel, pCurrentPos) ?
                Blocks.AIR.defaultBlockState() :
                super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    public boolean isValidBonemealTarget(BlockGetter pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
        return pState.getValue(AGE) < 2;
    }

    public boolean isBonemealSuccess(Level p_220995_, RandomSource p_220996_, BlockPos p_220997_, BlockState p_220998_) {
        return true;
    }

    public void performBonemeal(ServerLevel p_220990_, RandomSource p_220991_, BlockPos p_220992_, BlockState p_220993_) {
        p_220990_.setBlock(p_220992_, p_220993_.setValue(AGE, p_220993_.getValue(AGE) + 1), 2);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, AGE);
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}