package com.hollingsworth.craftedmagic.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class PhantomBlock extends Block {
    public PhantomBlock() {
        super(Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(0.0f).lightValue(7));
        setRegistryName("phantom_block");
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PhantomBlockTile();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}

