package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.common.block.tile.TimerSpellTurretTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class TimerSpellTurret extends BasicSpellTurret {

    public TimerSpellTurret(Properties properties) {
        super(properties);
    }

    public TimerSpellTurret() {
        super(defaultProperties().noOcclusion());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TimerSpellTurretTile(pos, state);
    }


    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (handIn == InteractionHand.MAIN_HAND) {
            if ((stack.getItem() instanceof ICasterTool) || worldIn.isClientSide)
                return super.useItemOn(stack,state, worldIn, pos, player, handIn, hit);
            if (worldIn.getBlockEntity(pos) instanceof TimerSpellTurretTile timerSpellTurretTile) {
                if (timerSpellTurretTile.isLocked)
                    return ItemInteractionResult.SUCCESS;
                timerSpellTurretTile.addTime(20 * (player.isShiftKeyDown() ? 10 : 1));
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof TimerSpellTurretTile tile) {
            if (!tile.isLocked) {
                tile.addTime(-20 * (player.isShiftKeyDown() ? 10 : 1));
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!world.isClientSide() && world.getBlockEntity(pos) instanceof TimerSpellTurretTile tile) {
            tile.isOff = world.hasNeighborSignal(pos);
            tile.updateBlock();
        }
    }
}
