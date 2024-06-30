package com.hollingsworth.arsnouveau.common.block;

import com.google.common.collect.ImmutableMap;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MagicFire extends BaseFireBlock {
    public static final MapCodec<MagicFire> CODEC = simpleCodec(MagicFire::new);
    public static final int MAX_AGE = 7;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((p_53467_) -> {
        return p_53467_.getKey() != Direction.DOWN;
    }).collect(Util.toMap());
    private static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    private final Map<BlockState, VoxelShape> shapesCache;

    public MagicFire(Properties pProperties) {
        this(pProperties, 1.0F);
    }

    public MagicFire(Properties pProperties, float pFireDamage) {
        super(pProperties, pFireDamage);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0).setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(UP, Boolean.FALSE));
        this.shapesCache = ImmutableMap.copyOf(this.stateDefinition.getPossibleStates().stream().filter((p_53497_) -> {
            return p_53497_.getValue(AGE) == 0;
        }).collect(Collectors.toMap(Function.identity(), MagicFire::calculateShape)));
    }

    @Override
    protected MapCodec<? extends BaseFireBlock> codec() {
        return CODEC;
    }

    private static VoxelShape calculateShape(BlockState p_53491_) {
        VoxelShape voxelshape = Shapes.empty();
        if (p_53491_.getValue(UP)) {
            voxelshape = UP_AABB;
        }

        if (p_53491_.getValue(NORTH)) {
            voxelshape = Shapes.or(voxelshape, NORTH_AABB);
        }

        if (p_53491_.getValue(SOUTH)) {
            voxelshape = Shapes.or(voxelshape, SOUTH_AABB);
        }

        if (p_53491_.getValue(EAST)) {
            voxelshape = Shapes.or(voxelshape, EAST_AABB);
        }

        if (p_53491_.getValue(WEST)) {
            voxelshape = Shapes.or(voxelshape, WEST_AABB);
        }

        return voxelshape.isEmpty() ? DOWN_AABB : voxelshape;
    }

    /**
     * Update the provided state given the provided neighbor direction and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific direction passed in.
     */
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return this.canSurvive(pState, pLevel, pCurrentPos) ? this.getStateWithAge(pLevel, pCurrentPos, pState.getValue(AGE)) : Blocks.AIR.defaultBlockState();
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return this.shapesCache.get(pState.setValue(AGE, 0));
    }

    @Override
    protected boolean canBurn(BlockState pState) {
        return false;
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.getStateForPlacement(pContext.getLevel(), pContext.getClickedPos());
    }

    public BlockState getStateForPlacement(BlockGetter pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        BlockState blockstate = pLevel.getBlockState(blockpos);
        if (!this.canCatchFire(pLevel, pPos, Direction.UP) && !blockstate.isFaceSturdy(pLevel, blockpos, Direction.UP)) {
            BlockState blockstate1 = this.defaultBlockState();

            for(Direction direction : Direction.values()) {
                BooleanProperty booleanproperty = PROPERTY_BY_DIRECTION.get(direction);
                if (booleanproperty != null) {
                    blockstate1 = blockstate1.setValue(booleanproperty, Boolean.valueOf(this.canCatchFire(pLevel, pPos.relative(direction), direction.getOpposite())));
                }
            }

            return blockstate1;
        } else {
            return this.defaultBlockState();
        }
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        return pLevel.getBlockState(blockpos).isFaceSturdy(pLevel, blockpos, Direction.UP) || this.isValidFireLocation(pLevel, pPos);
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        pLevel.scheduleTick(pPos, this, getFireTickDelay(pLevel.random));
        if (pLevel.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            BlockState blockstate = pLevel.getBlockState(pPos.below());
            boolean fireSourceBelow = blockstate.isFireSource(pLevel, pPos, Direction.UP);
            int age = pState.getValue(AGE);
            if (!fireSourceBelow && pLevel.isRaining() && this.isNearRain(pLevel, pPos) && pRandom.nextFloat() < 0.2F + (float) age * 0.03F) {
                pLevel.removeBlock(pPos, false);
                pLevel.removeBlock(pPos, false);
            } else {
                int j = Math.min(MAX_AGE, age + pRandom.nextInt(3) / 2);
                if (age != j) {
                    pState = pState.setValue(AGE, Integer.valueOf(j));
                    pLevel.setBlock(pPos, pState, 4);
                }

                if (!fireSourceBelow) {
                    if (!this.isValidFireLocation(pLevel, pPos)) {
                        BlockPos blockpos = pPos.below();
                        if (!pLevel.getBlockState(blockpos).isFaceSturdy(pLevel, blockpos, Direction.UP) || age > 3) {
                            pLevel.removeBlock(pPos, false);
                        }

                        return;
                    }

                    if (age == MAX_AGE && pRandom.nextInt(4) == 0 && !this.canCatchFire(pLevel, pPos.below(), Direction.UP)) {
                        pLevel.removeBlock(pPos, false);
                        return;
                    }
                }
            }
        }
    }

    protected boolean isNearRain(Level pLevel, BlockPos pPos) {
        return pLevel.isRainingAt(pPos) || pLevel.isRainingAt(pPos.west()) || pLevel.isRainingAt(pPos.east()) || pLevel.isRainingAt(pPos.north()) || pLevel.isRainingAt(pPos.south());
    }


    private BlockState getStateWithAge(LevelAccessor pLevel, BlockPos pPos, int pAge) {
        BlockState blockstate = getState(pLevel, pPos);
        return blockstate.setValue(AGE, pAge);
    }

    public static BlockState getState(BlockGetter pReader, BlockPos pPos) {
        return (BlockRegistry.MAGIC_FIRE.get().getStateForPlacement(pReader, pPos));
    }


    private boolean isValidFireLocation(BlockGetter pLevel, BlockPos pPos) {
        for(Direction direction : Direction.values()) {
            if (this.canCatchFire(pLevel, pPos.relative(direction), direction.getOpposite())) {
                return true;
            }
        }

        return false;
    }

    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
        pLevel.scheduleTick(pPos, this, getFireTickDelay(pLevel.random));
    }

    /**
     * Gets the delay before this block ticks again (without counting random ticks)
     */
    private static int getFireTickDelay(RandomSource pRandom) {
        return 30 + pRandom.nextInt(10);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE, NORTH, EAST, SOUTH, WEST, UP);
    }

    /**
     * Side sensitive version that calls the block function.
     *
     * @param world The current world
     * @param pos Block position
     * @param face The side the fire is coming from
     * @return True if the face can catch fire.
     */
    public boolean canCatchFire(BlockGetter world, BlockPos pos, Direction face) {
        return world.getBlockState(pos).isFlammable(world, pos, face);
    }
}
