package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.SkyBlockTile;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SkyWeave extends MirrorWeave implements ITickableBlock {

    public SkyWeave(Properties properties) {
        super(properties);
    }

    public SkyWeave() {
        super();
    }

    // In 1.21.11, getOcclusionShape(BlockState) is the override — no BlockGetter/BlockPos params.
    // BlockState.getOcclusionShape() also takes no params.
    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        // We don't have a BlockGetter here; fall back to super which checks the tile dynamically.
        // Tile-level logic is handled by the renderer / shouldRenderFace in MirrorWeaveTile.
        return super.getOcclusionShape(state);
    }

    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new SkyBlockTile(p_153215_, p_153216_);
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide() || pHand != InteractionHand.MAIN_HAND) {
            return InteractionResult.SUCCESS;
        }

        if (pLevel.getBlockEntity(pPos) instanceof SkyBlockTile tile) {
            if (!tile.showFacade() && !pPlayer.hasEffect(ModPotions.MAGIC_FIND_EFFECT)) {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
        }
        return super.useItemOn(stack, pState, pLevel, pPos, pPlayer, pHand, pHit);
    }
}
