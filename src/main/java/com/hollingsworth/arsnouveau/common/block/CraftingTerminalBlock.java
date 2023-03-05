package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.CraftingTerminalBlockEntity;
import com.hollingsworth.arsnouveau.common.block.tile.StorageTerminalBlockEntity;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CraftingTerminalBlock extends TickableModBlock {

	public CraftingTerminalBlock() {
		super(Properties.of(Material.WOOD).strength(3).noOcclusion());
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CraftingTerminalBlockEntity(pos, state);
	}

	@Override
	public void appendHoverText(ItemStack stack, BlockGetter worldIn, List<Component> tooltip,
			TooltipFlag flagIn) {
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
		return PushReaction.BLOCK;
	}

	@NotNull
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING,  context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos,
								 Player player, InteractionHand hand, BlockHitResult rtr) {
		if (world.isClientSide || player.getItemInHand(hand).getItem() instanceof DominionWand || hand != InteractionHand.MAIN_HAND) {
			return InteractionResult.PASS;
		}

		BlockEntity blockEntity_1 = world.getBlockEntity(pos);
		if (blockEntity_1 instanceof StorageTerminalBlockEntity term) {
			if(!term.openMenu(player, new ArrayList<>())){
				player.displayClientMessage(Component.translatable("ars_nouveau.invalid_lectern"), true);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState state2, boolean flag) {
		if (!state.is(state2.getBlock())) {
			BlockEntity blockentity = world.getBlockEntity(pos);
			if (blockentity instanceof CraftingTerminalBlockEntity te) {
				Containers.dropContents(world, pos, te.getCraftingInv());
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
