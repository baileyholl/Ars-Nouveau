package com.hollingsworth.arsnouveau.common.tss.platform;

import com.hollingsworth.arsnouveau.common.block.TickableModBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractStorageTerminalBlock extends TickableModBlock implements SimpleWaterloggedBlock {
	public static final EnumProperty<TerminalPos> TERMINAL_POS = EnumProperty.create("pos", TerminalPos.class);
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SHAPE_N = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 6.0D);
	private static final VoxelShape SHAPE_S = Block.box(0.0D, 0.0D, 10.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape SHAPE_E = Block.box(10.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape SHAPE_W = Block.box(0.0D, 0.0D, 0.0D, 6.0D, 16.0D, 16.0D);
	private static final VoxelShape SHAPE_U = Block.box(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape SHAPE_D = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);
	public AbstractStorageTerminalBlock() {
		super(Properties.of(Material.WOOD).strength(3).lightLevel(s -> 6));
		registerDefaultState(defaultBlockState().setValue(TERMINAL_POS, TerminalPos.CENTER).setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH));
	}

	@Override
	public RenderShape getRenderShape(BlockState p_149645_1_) {
		return RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED, TERMINAL_POS);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult rtr) {
		if (world.isClientSide) {
			return InteractionResult.SUCCESS;
		}

		BlockEntity blockEntity_1 = world.getBlockEntity(pos);
		if (blockEntity_1 instanceof StorageTerminalBlockEntity term) {
			if(term.canInteractWith(player)) {
				player.openMenu(term);
			} else {
				player.displayClientMessage(Component.translatable("chat.toms_storage.terminal_out_of_range"), true);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction direction = context.getClickedFace().getOpposite();
		FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
		TerminalPos pos = TerminalPos.CENTER;
		if(direction.getAxis() == Direction.Axis.Y) {
			if(direction == Direction.UP)pos = TerminalPos.UP;
			if(direction == Direction.DOWN)pos = TerminalPos.DOWN;
			direction = context.getHorizontalDirection();
		}
		return this.defaultBlockState().setValue(FACING, direction.getAxis() == Direction.Axis.Y ? Direction.NORTH : direction).
				setValue(TERMINAL_POS, pos).
				setValue(WATERLOGGED, Boolean.valueOf(ifluidstate.getType() == Fluids.WATER));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.getValue(WATERLOGGED)) {
			worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}

		return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		switch (state.getValue(TERMINAL_POS)) {
		case CENTER:
			switch (state.getValue(FACING)) {
			case NORTH:
				return SHAPE_N;
			case SOUTH:
				return SHAPE_S;
			case EAST:
				return SHAPE_E;
			case WEST:
				return SHAPE_W;
			default:
				break;
			}
			break;

		case UP:
			return SHAPE_U;

		case DOWN:
			return SHAPE_D;

		default:
			break;
		}

		return SHAPE_N;
	}

	public static enum TerminalPos implements StringRepresentable {
		CENTER("center"),
		UP("up"),
		DOWN("down")
		;
		private String name;
		private TerminalPos(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}
}
