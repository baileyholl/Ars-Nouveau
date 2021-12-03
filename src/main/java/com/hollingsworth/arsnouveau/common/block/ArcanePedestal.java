package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public class ArcanePedestal extends TickableModBlock {

    public ArcanePedestal() {
        super(TickableModBlock.defaultProperties().noOcclusion(),LibBlockNames.ARCANE_PEDESTAL);
    }


    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if(handIn != InteractionHand.MAIN_HAND)
            return InteractionResult.PASS;
        if(!world.isClientSide) {
            ArcanePedestalTile tile = (ArcanePedestalTile) world.getBlockEntity(pos);
            if (tile.stack != null && player.getItemInHand(handIn).isEmpty()) {
                if(world.getBlockState(pos.above()).getMaterial() != Material.AIR)
                    return InteractionResult.SUCCESS;
                ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.stack);
                world.addFreshEntity(item);
                tile.stack = null;
            } else if (!player.getInventory().getSelected().isEmpty()) {
                if(tile.stack != null){
                    ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.stack);
                    world.addFreshEntity(item);
                }

                tile.stack = player.getInventory().removeItem(player.getInventory().selected, 1);

            }
            world.sendBlockUpdated(pos, state, state, 2);
        }
        return  InteractionResult.SUCCESS;
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if(worldIn.getBlockEntity(pos) instanceof ArcanePedestalTile && ((ArcanePedestalTile) worldIn.getBlockEntity(pos)).stack != null){
            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), ((ArcanePedestalTile) worldIn.getBlockEntity(pos)).stack));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Block.box(1D, 0.0D, 1.0D, 15, 16, 15);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArcanePedestalTile(pos, state);
    }
}
