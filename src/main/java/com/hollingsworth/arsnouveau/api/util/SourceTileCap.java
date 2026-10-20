package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;

final class SourceTileCap implements ISourceTile {
    private final ISourceCap cap;

    SourceTileCap(ISourceCap cap) {
        this.cap = cap;
    }

    @Override
    public int getTransferRate() {
        return cap.getMaxExtract();
    }

    @Override
    public boolean canAcceptSource() {
        return cap.canReceive();
    }

    @Override
    public boolean canProvideSource() {
        return cap.canExtract() && cap.getSource() > 0;
    }

    @Override
    public int getSource() {
        return cap.getSource();
    }

    @Override
    public int getMaxSource() {
        return cap.getSourceCapacity();
    }

    @Override
    public int setSource(int source) {
        cap.setSource(source);
        return cap.getSource();
    }

    @Override
    public int addSource(int source, boolean simulate) {
        return cap.receiveSource(source, simulate);
    }

    @Override
    public int addSource(int source) {
        cap.receiveSource(source, false);
        return cap.getSource();
    }

    @Override
    public int removeSource(int source) {
        cap.extractSource(source, false);
        return cap.getSource();
    }

    @Override
    public int removeSource(int source, boolean simulate) {
        return cap.extractSource(source, simulate);
    }

    @Override
    public boolean isInfinite() {
        return cap.isInfinite();
    }
}
