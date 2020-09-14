package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ArcanePedestal extends ModBlock{

    public ArcanePedestal() {
        super(ModBlock.defaultProperties().notSolid(),LibBlockNames.ARCANE_PEDESTAL);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(handIn != Hand.MAIN_HAND)
            return ActionResultType.PASS;
        if(!world.isRemote) {
            ArcanePedestalTile tile = (ArcanePedestalTile) world.getTileEntity(pos);
            if (tile.stack != null && player.getHeldItem(handIn).isEmpty()) {
                if(world.getBlockState(pos.up()).getMaterial() != Material.AIR)
                    return ActionResultType.SUCCESS;
                ItemEntity item = new ItemEntity(world, player.getPosX(), player.getPosY(), player.getPosZ(), tile.stack);
                world.addEntity(item);
                tile.stack = null;
            } else if (!player.inventory.getCurrentItem().isEmpty()) {

                if(tile.stack != null){
                    ItemEntity item = new ItemEntity(world, player.getPosX(), player.getPosY(), player.getPosZ(), tile.stack);
                    world.addEntity(item);
                }

                tile.stack = player.inventory.decrStackSize(player.inventory.currentItem, 1);

            }
            world.notifyBlockUpdate(pos, state, state, 2);
        }
        return  ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ArcanePedestalTile();
    }
}
