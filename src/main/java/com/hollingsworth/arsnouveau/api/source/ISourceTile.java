package com.hollingsworth.arsnouveau.api.source;

/**
 * Interface for a generic tile that holds source.
 */
public interface ISourceTile {

    int getTransferRate();

    boolean canAcceptSource();

    boolean sourcelinksCanProvideSource();

    boolean canProvideSource();

    boolean machinesCanTakeSource();

    int getSource();

    int getMaxSource();

    void setMaxSource(int max);

    int setSource(final int source);

    int addSource(final int source);

    int removeSource(final int source);
}
