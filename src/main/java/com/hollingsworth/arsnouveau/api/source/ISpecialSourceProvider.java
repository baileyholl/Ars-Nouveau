package com.hollingsworth.arsnouveau.api.source;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface ISpecialSourceProvider {

    ISourceTile getSource();

    boolean isValid();

    BlockPos getCurrentPos();

    Direction getCurrentDir();

}
