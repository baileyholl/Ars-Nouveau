package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.CraftingLecternTile;
import com.hollingsworth.arsnouveau.common.block.tile.StorageLecternTile;
import com.hollingsworth.arsnouveau.common.block.tile.TransientCustomContainer;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.items.summon_charms.BookwyrmCharm;
import com.hollingsworth.arsnouveau.common.util.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class CraftingLecternBlock extends TickableModBlock {

	public static VoxelShape SHAPE_NORTH = Stream.of(
			Block.box(4, 0, 2, 12, 2, 10),
			Block.box(5, 2, 3, 11, 14, 9),
			Block.box(5, 14, 4, 11, 15, 9),
			Block.box(0, 12, 1, 16, 15, 5),
			Block.box(0, 14, 5, 16, 17, 9),
			Block.box(0, 16, 9, 16, 19, 13)
	).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

	public static final VoxelShape SHAPE_SOUTH = VoxelShapeUtils.rotateHorizontal(SHAPE_NORTH, Direction.SOUTH);
	public static final VoxelShape SHAPE_EAST = VoxelShapeUtils.rotateHorizontal(SHAPE_NORTH, Direction.EAST);
	public static final VoxelShape SHAPE_WEST = VoxelShapeUtils.rotateHorizontal(SHAPE_NORTH, Direction.WEST);

	public CraftingLecternBlock() {
		super(Properties.of().strength(3).noOcclusion().pushReaction(PushReaction.BLOCK).ignitedByLava().sound(SoundType.WOOD));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CraftingLecternTile(pos, state);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
		return PushReaction.BLOCK;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, pContext.getHorizontalDirection().getOpposite());
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Direction direction = state.getValue(HorizontalDirectionalBlock.FACING);
        return switch (direction) {
            case NORTH -> SHAPE_NORTH;
			case SOUTH -> SHAPE_SOUTH;
			case EAST -> SHAPE_EAST;
			case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos,
									 Player player, InteractionHand hand, BlockHitResult rtr) {
		ItemStack heldStack = player.getItemInHand(hand);
		if(world.isClientSide){
			return ItemInteractionResult.SUCCESS;
		}
		if (heldStack.getItem() instanceof DominionWand
				|| hand != InteractionHand.MAIN_HAND
				|| heldStack.getItem() instanceof BookwyrmCharm) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		BlockEntity blockEntity_1 = world.getBlockEntity(pos);
		if (blockEntity_1 instanceof StorageLecternTile term) {
			if(!term.openMenu(player)){
				player.displayClientMessage(Component.translatable("ars_nouveau.invalid_lectern"), true);
			}
		}
		return ItemInteractionResult.SUCCESS;
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState state2, boolean flag) {
		if (!state.is(state2.getBlock())) {
			BlockEntity blockentity = world.getBlockEntity(pos);
			if (blockentity instanceof CraftingLecternTile te) {
				for (TransientCustomContainer inv : te.craftingMatrices.values()) {
					Containers.dropContents(world, pos, inv);
				}
				world.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, world, pos, state2, flag);
		}
	}

	@Override
	public RenderShape getRenderShape(BlockState p_149645_1_) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HorizontalDirectionalBlock.FACING);
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(HorizontalDirectionalBlock.FACING, rot.rotate(state.getValue(HorizontalDirectionalBlock.FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(HorizontalDirectionalBlock.FACING)));
	}
}
