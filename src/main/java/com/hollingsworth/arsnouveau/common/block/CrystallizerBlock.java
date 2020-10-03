package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.CrystallizerTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class CrystallizerBlock extends ModBlock{
    public CrystallizerBlock() {
        super(defaultProperties().notSolid(), LibBlockNames.CRYSTALLIZER);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CrystallizerTile();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }


    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!(worldIn.getTileEntity(pos) instanceof CrystallizerTile))
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
        ItemStack stack = ((CrystallizerTile) worldIn.getTileEntity(pos)).stack;
        worldIn.addEntity(new ItemEntity(worldIn, player.getPosX(), player.getPosY(), player.getPosZ(), stack.copy()));
        ((CrystallizerTile) worldIn.getTileEntity(pos)).stack = ItemStack.EMPTY;
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(worldIn, pos, state, player);
        if(!(worldIn.getTileEntity(pos) instanceof CrystallizerTile))
            return;
        ItemStack stack = ((CrystallizerTile) worldIn.getTileEntity(pos)).stack;
        worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack.copy()));
        ((CrystallizerTile) worldIn.getTileEntity(pos)).stack = ItemStack.EMPTY;
    }
}
