package com.hollingsworth.arsnouveau.common.tss.platform;

import com.hollingsworth.arsnouveau.common.block.TickableModBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

public abstract class AbstractStorageTerminalBlock extends TickableModBlock implements SimpleWaterloggedBlock {
	public static final EnumProperty<TerminalPos> TERMINAL_POS = EnumProperty.create("pos", TerminalPos.class);
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;


	public AbstractStorageTerminalBlock() {
		super(Properties.of(Material.WOOD).strength(3).lightLevel(s -> 6));
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH));
	}

	@Override
	public RenderShape getRenderShape(BlockState p_149645_1_) {
		return RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
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
