package com.hollingsworth.arsnouveau.api.source;

import net.minecraft.core.BlockPos;

import java.util.List;

public interface IMultiSourceTargetProvider {
    List<BlockPos> getFromList();
    List<BlockPos> getToList();
}
