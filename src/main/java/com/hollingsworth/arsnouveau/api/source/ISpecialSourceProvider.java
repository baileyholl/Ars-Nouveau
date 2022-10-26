package com.hollingsworth.arsnouveau.api.source;

import net.minecraft.core.BlockPos;

public interface ISpecialSourceProvider {

    ISourceTile getSource();

    boolean isValid();

    BlockPos getCurrentPos();

}
