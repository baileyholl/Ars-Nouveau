package com.hollingsworth.arsnouveau.api.source;

public interface ISourceCap {


    boolean canAcceptSource(int source);

    boolean canProvideSource(int source);

    int getMaxExtract();

    int getMaxReceive();

    default boolean canExtract() {
        return canProvideSource(1);
    }

    default boolean canReceive() {
        return canAcceptSource(1);
    }

    int getSource();

    int getSourceCapacity();

    default int getMaxSource() {
        return getSourceCapacity();
    }

    /**
     * Force set the amount of source stored, clamped to the max source.
     * Use for source generation or other use-cases without transfer rates.
     */
    void setSource(int source);

    void setMaxSource(int max);

    int receiveSource(final int source, boolean simulate);

    int extractSource(final int source, boolean simulate);

}
