package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.CraftingLecternTile;
import com.hollingsworth.arsnouveau.common.block.tile.StorageLecternTile;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.items.summon_charms.BookwyrmCharm;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class CraftingLecternBlock extends TickableModBlock {

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

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState state2, boolean flag) {
		if (!state.is(state2.getBlock())) {
			BlockEntity blockentity = world.getBlockEntity(pos);
			if (blockentity instanceof CraftingLecternTile te) {
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
