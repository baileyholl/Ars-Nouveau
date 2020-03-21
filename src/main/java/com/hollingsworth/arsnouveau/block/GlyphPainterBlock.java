package com.hollingsworth.arsnouveau.block;

import com.hollingsworth.arsnouveau.block.tile.GlyphPainterTile;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class GlyphPainterBlock extends ModBlock{
    public GlyphPainterBlock() {
        super("glyph_painter");
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new GlyphPainterTile();
    }
}
