package com.hollingsworth.arsnouveau.api.source;

import net.minecraft.core.BlockPos;

public interface ISpecialSourceProvider {

    ISourceCap getCapability();

    boolean isValid();

    BlockPos getCurrentPos();

}
