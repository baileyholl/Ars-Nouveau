package com.hollingsworth.arsnouveau.api.source;

import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import net.minecraft.core.BlockPos;

public class SourceProvider implements ISpecialSourceProvider {
    private final ISourceCap capability;
    private final BlockPos pos;
    private final boolean isValid;

    public SourceProvider(ISourceCap capability, BlockPos pos) {
        this.capability = capability;
        this.pos = pos;
        this.isValid = capability != null;
    }

    public SourceProvider(ISpecialSourceProvider specialSourceProvider){
        this.capability = specialSourceProvider.getCapability();
        this.pos = specialSourceProvider.getCurrentPos();
        isValid = specialSourceProvider.isValid();
    }

    @Override
    public ISourceCap getCapability() {
        return capability;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public BlockPos getCurrentPos() {
        return pos;
    }
}
