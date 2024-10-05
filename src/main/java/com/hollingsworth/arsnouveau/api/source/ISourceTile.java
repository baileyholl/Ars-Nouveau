package com.hollingsworth.arsnouveau.api.source;

/**
 * Interface for a generic tile that holds source.
 */
public interface ISourceTile {

    int getTransferRate();

    boolean canAcceptSource();

    default boolean canProvideSource() {
        return this.getSource() > 0;
    }

    int getSource();

    int getMaxSource();

    void setMaxSource(int max);

    int setSource(final int source);

    /**
     * Adds source to the tile, can be simulated to check the max amount that can be added.
     *
     * @param source   The amount of source to add.
     * @param simulate If the action should be simulated.
     * @return The amount of source added.
     * * TODO: Remove default implementation from method after addons are updated. Otherwise simulate may not be implemented by all tiles.
     */
    default int addSource(final int source, boolean simulate) {
        return addSource(source);
    }

    int addSource(final int source);

    int removeSource(final int source);

    /**
     * Removes source from the tile, can be simulated to check the max amount that can be removed.
     *
     * @param source   The amount of source to remove.
     * @param simulate If the action should be simulated.
     * @return The amount of source removed.
     * TODO: Remove default implementation from method after addons are updated. Otherwise simulate will not work unless overriden.
     */
    default int removeSource(final int source, boolean simulate) {
        return removeSource(source);
    }

}
