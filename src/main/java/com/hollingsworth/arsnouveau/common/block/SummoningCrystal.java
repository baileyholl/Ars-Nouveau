package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.SummoningCrystalTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SummoningCrystal extends ModBlock{
    public SummoningCrystal() {
        super(ModBlock.defaultProperties().notSolid(), LibBlockNames.SUMMONING_CRYSTAL);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState p_225533_1_, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult p_225533_6_) {
        if(!world.isRemote && hand == Hand.MAIN_HAND && player.getHeldItem(hand).isEmpty()){
            if(world.getTileEntity(pos) instanceof SummoningCrystalTile)
                ((SummoningCrystalTile) world.getTileEntity(pos)).changeTier(player);
        }
        return super.onBlockActivated(p_225533_1_, world, pos, player, hand, p_225533_6_);

    }


    @Override
    public void neighborChanged(BlockState p_220069_1_, World world, BlockPos pos, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        super.neighborChanged(p_220069_1_, world, pos, p_220069_4_, p_220069_5_, p_220069_6_);
        if(!world.isRemote() && world.getTileEntity(pos) instanceof SummoningCrystalTile){
            ((SummoningCrystalTile) world.getTileEntity(pos)).isOff = world.isBlockPowered(pos);
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SummoningCrystalTile();
    }
}
