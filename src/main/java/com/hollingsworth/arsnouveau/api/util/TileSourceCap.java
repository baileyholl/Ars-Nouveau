package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;

record TileSourceCap(ISourceTile tile) implements ISourceCap {

    @Override
    public boolean canAcceptSource(int source) {
        return receiveSource(source, true) > 0;
    }

    @Override
    public boolean canProvideSource(int source) {
        return extractSource(source, true) > 0;
    }

    @Override
    public int getMaxExtract() {
        return tile.getTransferRate();
    }

    @Override
    public int getMaxReceive() {
        return tile.getTransferRate();
    }

    @Override
    public int getSource() {
        return tile.getSource();
    }

    @Override
    public int getSourceCapacity() {
        return tile.getMaxSource();
    }

    @Override
    public void setSource(int source) {
        tile.setSource(source);
    }

    @Override
    public void setMaxSource(int max) {
        throw new UnsupportedOperationException("The maximum source of an ISourceTile cannot be set");
    }

    @Override
    public int receiveSource(int source, boolean simulate) {
        int before = tile.getSource();
        int received = Math.min(source, tile.getMaxSource() - before);
        if (simulate || received <= 0) {
            return Math.max(0, received);
        }
        tile.addSource(received);
        return tile.getSource() - before;
    }

    @Override
    public int extractSource(int source, boolean simulate) {
        int before = tile.getSource();
        int extracted = Math.min(source, before);
        if (simulate || extracted <= 0) {
            return Math.max(0, extracted);
        }
        tile.removeSource(extracted);
        return before - tile.getSource();
    }

    @Override
    public boolean isInfinite() {
        return tile.isInfinite();
    }
}
