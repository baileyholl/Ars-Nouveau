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
        super(defaultProperties().noOcclusion(), LibBlockNames.CRYSTALLIZER);
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
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }


    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!(worldIn.getBlockEntity(pos) instanceof CrystallizerTile))
            return super.use(state, worldIn, pos, player, handIn, hit);
        ItemStack stack = ((CrystallizerTile) worldIn.getBlockEntity(pos)).stack;
        worldIn.addFreshEntity(new ItemEntity(worldIn, player.getX(), player.getY(), player.getZ(), stack.copy()));
        ((CrystallizerTile) worldIn.getBlockEntity(pos)).stack = ItemStack.EMPTY;
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if(!(worldIn.getBlockEntity(pos) instanceof CrystallizerTile))
            return;
        ItemStack stack = ((CrystallizerTile) worldIn.getBlockEntity(pos)).stack;
        worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack.copy()));
        ((CrystallizerTile) worldIn.getBlockEntity(pos)).stack = ItemStack.EMPTY;
    }
}
