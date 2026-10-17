package com.hollingsworth.arsnouveau.api.source;

import net.minecraft.core.BlockPos;

public interface ISpecialSourceProvider {

    ISourceCap getSource();

    boolean isValid();

    BlockPos getCurrentPos();

}
