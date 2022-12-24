package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class RotatingSpellTurret extends BasicSpellTurret {
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player.getItemInHand(handIn).getItem() instanceof WarpScroll) {
            BlockPos aimPos = new WarpScroll.WarpScrollData(player.getItemInHand(handIn)).getPos();
            if (player.getLevel().getBlockEntity(pos) instanceof RotatingTurretTile tile) {
                tile.aim(aimPos, player);
            }
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }


    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        Direction orientation = placer == null ? Direction.WEST : Direction.orderedByNearest(placer)[0].getOpposite();

        if (!(world.getBlockEntity(pos) instanceof RotatingTurretTile turretTile)) return;
        switch (orientation) {
            case DOWN:
                turretTile.rotationY = -90F;
                break;
            case UP:
                turretTile.rotationY = 90F;
                break;
            case NORTH:
                turretTile.rotationX = 270F;
                break;
            case SOUTH:
                turretTile.rotationX = 90F;
                break;
            case WEST:
                break;
            case EAST:
                turretTile.rotationX = 180F;
                break;
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RotatingTurretTile(pos, state);
    }

}
