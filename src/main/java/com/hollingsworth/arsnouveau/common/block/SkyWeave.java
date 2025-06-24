package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.SkyBlockTile;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof SkyBlockTile sbt && sbt.showFacade()) {
            return sbt.mimicState.canOcclude() ? sbt.mimicState.getOcclusionShape(level, pos) : Shapes.empty();
        }
        return super.getOcclusionShape(state, level, pos);
    }

    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new SkyBlockTile(p_153215_, p_153216_);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide || pHand != InteractionHand.MAIN_HAND) {
            return ItemInteractionResult.SUCCESS;
        }

        if (pLevel.getBlockEntity(pPos) instanceof SkyBlockTile tile) {
            if (!tile.showFacade() && !pPlayer.hasEffect(ModPotions.MAGIC_FIND_EFFECT)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        }
        return super.useItemOn(stack, pState, pLevel, pPos, pPlayer, pHand, pHit);
    }
}
