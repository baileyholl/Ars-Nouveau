package com.hollingsworth.arsnouveau.block;

import com.hollingsworth.arsnouveau.block.tile.GlyphPressTile;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class GlyphPressBlock extends ModBlock{
    public GlyphPressBlock() {
        super("glyph_press");
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new GlyphPressTile();
    }
}
