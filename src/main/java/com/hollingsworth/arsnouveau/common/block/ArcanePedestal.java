package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.util.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.stream.Stream;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class ArcanePedestal extends ModBlock implements EntityBlock, SimpleWaterloggedBlock {

    public ArcanePedestal() {
        super(ModBlock.defaultProperties().noOcclusion().forceSolidOn());
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false).setValue(BlockStateProperties.FACING, Direction.UP));
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack pStack,BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (handIn != InteractionHand.MAIN_HAND)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!world.isClientSide && world.getBlockEntity(pos) instanceof ArcanePedestalTile tile) {
            if (tile.getStack() != null && player.getItemInHand(handIn).isEmpty()) {
                ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.getStack());
                world.addFreshEntity(item);
                tile.setStack(ItemStack.EMPTY);
            } else if (!player.getInventory().getSelected().isEmpty()) {
                if (tile.getStack() != null) {
                    ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.getStack());
                    world.addFreshEntity(item);
                }
                tile.setStack(player.getInventory().removeItem(player.getInventory().selected, 1));
            }
            world.sendBlockUpdated(pos, state, state, 2);
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public BlockState playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if (worldIn.getBlockEntity(pos) instanceof ArcanePedestalTile tile && tile.getStack() != null) {
            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.getStack()));
        }
        return state;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return switch (facing) {
            case UP -> UP;
            case DOWN -> DOWN;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    public Vector3f getItemOffset(BlockState pedestalState, BlockPos pos){
        Direction direction = pedestalState.getValue(BlockStateProperties.FACING);
        Vector3f dirVec = direction.step().mul( getOffsetScalar());
        float x = pos.getX() + 0.5f + dirVec.x;
        float y = pos.getY() + 0.5f + dirVec.y;
        float z = pos.getZ() + 0.5f + dirVec.z;
        return new Vector3f(x, y, z);
    }

    public float getOffsetScalar(){
        return 0.6f;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArcanePedestalTile(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, BlockStateProperties.FACING);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

   @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER).setValue(BlockStateProperties.FACING, context.getClickedFace());
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(BlockStateProperties.FACING, rot.rotate(state.getValue(BlockStateProperties.FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction side, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        return stateIn;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        ArcanePedestalTile tile = (ArcanePedestalTile) worldIn.getBlockEntity(pos);
        if (tile == null || tile.getStack().isEmpty()) return 0;
        return 15;
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        if (!pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof ArcanePedestalTile tile) {
            if(tile.hasSignal != pLevel.hasNeighborSignal(pPos)) {
                tile.hasSignal = !tile.hasSignal;
                tile.updateBlock();
            }
        }
    }

    public static final VoxelShape UP = Stream.of(
            Block.box(2, 11, 2, 14, 13, 14),
            Block.box(5, 0, 5, 11, 3, 11),
            Block.box(5, 8, 5, 11, 11, 11),
            Block.box(6, 3, 6, 10, 8, 10),
            Stream.of(
                    Block.box(7, 8, 1, 9, 11, 5),
                    Block.box(7, 3, 3, 9, 8, 5),
                    Block.box(7, 0, 1, 9, 3, 5)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(1, 8, 7, 5, 11, 9),
                    Block.box(3, 3, 7, 5, 8, 9),
                    Block.box(1, 0, 7, 5, 3, 9)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(7, 8, 11, 9, 11, 15),
                    Block.box(7, 3, 11, 9, 8, 13),
                    Block.box(7, 0, 11, 9, 3, 15)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(11, 8, 7, 15, 11, 9),
                    Block.box(11, 3, 7, 13, 8, 9),
                    Block.box(11, 0, 7, 15, 3, 9)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape DOWN = VoxelShapeUtils.rotate(UP, Direction.UP);
    public static final VoxelShape EAST = VoxelShapeUtils.rotate(UP, Direction.WEST);
    public static final VoxelShape WEST = VoxelShapeUtils.rotate(UP, Direction.EAST);
    public static final VoxelShape NORTH = VoxelShapeUtils.rotate(UP, Direction.SOUTH);
    public static final VoxelShape SOUTH = VoxelShapeUtils.rotate(UP, Direction.NORTH);

    @Override
    protected boolean isPathfindable(BlockState pState, PathComputationType pPathComputationType) {
        return false;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof ArcanePedestalTile tile) {
            if (pStack.get(DataComponents.CUSTOM_NAME) != null) {
                tile.setCustomName(pStack.getHoverName());
            }
            tile.updateBlock();
        }
    }
}
