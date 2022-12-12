package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.MirrorWeaveTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MirrorWeave extends ModBlock implements EntityBlock {
    public BlockState blockState = Blocks.SAND.defaultBlockState();

    public MirrorWeave() {
        super(Block.Properties.of(Material.DECORATION).noOcclusion());
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide || pHand != InteractionHand.MAIN_HAND) {
            return InteractionResult.SUCCESS;
        }
        if(!pLevel.isClientSide() &&  pHand == InteractionHand.MAIN_HAND){
            MirrorWeaveTile tile = (MirrorWeaveTile) pLevel.getBlockEntity(pPos);
            if(tile != null){
                ItemStack stack = pPlayer.getItemInHand(pHand);
                if(stack.getItem() instanceof BlockItem blockItem){
                    tile.mimicState = blockItem.getBlock().getStateForPlacement(new BlockPlaceContext(pLevel, pPlayer, pHand, stack, pHit));
                    tile.updateBlock();
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MirrorWeaveTile(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
