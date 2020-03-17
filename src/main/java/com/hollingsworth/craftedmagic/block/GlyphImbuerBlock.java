package com.hollingsworth.craftedmagic.block;

import com.hollingsworth.craftedmagic.block.tile.GlyphImbuerTile;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class GlyphImbuerBlock extends ModBlock{
    public GlyphImbuerBlock() {
        super("glyph_imbuer");
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new GlyphImbuerTile();
    }
}
