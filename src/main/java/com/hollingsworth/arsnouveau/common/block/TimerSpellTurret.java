package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.common.block.tile.TimerSpellTurretTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class TimerSpellTurret extends BasicSpellTurret {

    public TimerSpellTurret(Properties properties) {
        super(properties);
    }

    public TimerSpellTurret() {
        super(defaultProperties().noOcclusion());
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TimerSpellTurretTile(pos, state);
    }


    @Override
    public @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
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
    public void attack(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof TimerSpellTurretTile tile) {
            if (!tile.isLocked) {
                tile.addTime(-20 * (player.isShiftKeyDown() ? 10 : 1));
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level world, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos, boolean isMoving) {
        if (!world.isClientSide() && world.getBlockEntity(pos) instanceof TimerSpellTurretTile tile) {
            tile.isOff = world.hasNeighborSignal(pos);
            tile.updateBlock();
        }
    }
}
