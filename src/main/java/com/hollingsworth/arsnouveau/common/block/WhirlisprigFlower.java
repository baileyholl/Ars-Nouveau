package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.WhirlisprigTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.Direction;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.item.context.BlockPlaceContext;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.BlockGetter;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.Level;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.util.RandomSource;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.LevelAccessor;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.LevelReader;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.ScheduledTickAccess;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.redstone.Orientation;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.block.Block;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.block.RenderShape;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.block.SoundType;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.block.state.BlockState;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.block.state.StateDefinition;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.material.FluidState;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.material.Fluids;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.level.material.MapColor;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.phys.shapes.BooleanOp;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.phys.shapes.CollisionContext;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.phys.shapes.Shapes;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

import static com.hollingsworth.arsnouveau.common.block.tile.SummoningTile.CONVERTED;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class WhirlisprigFlower extends SummonBlock implements SimpleWaterloggedBlock {

    public WhirlisprigFlower(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(CONVERTED, false).setValue(BlockStateProperties.WATERLOGGED, false));
    }

    public WhirlisprigFlower() {
        this(BlockRegistry.newBlockProperties().sound(SoundType.SPORE_BLOSSOM).noOcclusion().strength(2.0f, 6.0f).mapColor(MapColor.PLANT));
    }

    VoxelShape shape = Stream.of(
            Block.box(7, 0, 7, 9, 10, 9),
            Block.box(6, 10, 6, 10, 12, 10),
            Block.box(1.92, 11.107, 6, 5.93, 12.107, 10),
            Block.box(10.07, 11.107, 6, 14.072, 12.107, 10),
            Block.box(6, 11.107, 10.077, 10, 12.1, 14.07),
            Block.box(6, 11.107, 1.93, 10, 12.1, 5.93)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new WhirlisprigTile(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    protected BlockState updateShape(BlockState stateIn, LevelReader pLevel, ScheduledTickAccess pScheduledTickAccess, BlockPos currentPos, Direction side, BlockPos facingPos, BlockState facingState, RandomSource pRandom) {
        if (stateIn.getValue(WATERLOGGED)) {
            pScheduledTickAccess.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return stateIn;
    }

    @Override
    protected void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, Orientation pOrientation, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pOrientation, pIsMoving);
        if (!pLevel.isClientSide() && pLevel.getBlockEntity(pPos) instanceof WhirlisprigTile flower) {
            flower.isOff = pLevel.hasNeighborSignal(pPos);
            flower.updateBlock();
        }
    }
}
