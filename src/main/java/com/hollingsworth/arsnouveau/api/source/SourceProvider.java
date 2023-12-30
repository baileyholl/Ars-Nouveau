package com.hollingsworth.arsnouveau.api.source;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class SourceProvider implements ISpecialSourceProvider {
    private final ISourceTile tile;
    private final BlockPos pos;
    private final Direction dir;
    private final boolean isValid;

    public SourceProvider(ISourceTile tile, BlockPos pos, Direction dir) {
        this.tile = tile;
        this.pos = pos;
        this.dir = dir;
        this.isValid = tile != null;
    }

    public SourceProvider(ISpecialSourceProvider specialSourceProvider){
        this.tile = specialSourceProvider.getSource();
        this.pos = specialSourceProvider.getCurrentPos();
        this.dir = specialSourceProvider.getCurrentDir();
        isValid = specialSourceProvider.isValid();
    }

    @Override
    public ISourceTile getSource() {
        return tile;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public BlockPos getCurrentPos() {
        return pos;
    }

    @Override
    public Direction getCurrentDir() {
        return dir;
    }
}
