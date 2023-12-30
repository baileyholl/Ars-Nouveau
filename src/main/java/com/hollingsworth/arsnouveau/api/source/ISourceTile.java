package com.hollingsworth.arsnouveau.api.source;

/**
 * Interface for a generic tile that holds source.
 */
public interface ISourceTile {
    /**
     * @return The rate at which source should be transferred to/from this tile.
     */
    int getTransferRate();

    /**
     * @return Whether this machine can receive source from other tiles.
     */
    boolean canAcceptSource();

    /**
     * @return Whether this machine can receive source directly from Sourcelinks.
     */
    default boolean sourcelinksCanProvideSource() {
        return false;
    }

    /**
     * @return Whether this machine can send source to other tiles.
     */
    boolean canProvideSource();

    /**
     * @return Whether other machines can directly take source from this tile.
     */
    default boolean machinesCanTakeSource() {
        return canProvideSource();
    }

    /**
     * @return The current amount of source stored by this tile.
     */
    int getSource();

    /**
     * @return The maximum amount of source stored by this tile.
     */
    int getMaxSource();

    /**
     * Sets the maximum amount of source for this tile.
     * TODO: Currently unused?
     */
    void setMaxSource(int max);

    /**
     * Sets the current amount of source for this tile.
     * @return The new amount of source.
     */
    int setSource(final int source);

    /**
     * Adds the given amount of source to this tile.
     * @return The new overall amount of source.
     */
    int addSource(final int source);

    /**
     * Removes the given amount of source from this tile.
     * @return The new overall amount of source.
     */
    int removeSource(final int source);
}
