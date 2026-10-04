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

    /**
     * @return Whether this capability can be used to provide source automatically, like a Source Jar
     */
    default boolean providesAutomatically() {
        return false;
    }

    /**
     * @return Whether this capability can accept source automatically, like a Source Jar
     */
    default boolean acceptsAutomaticcally() {
        return false;
    }

    /**
     * @return Whether this capability can provide an infinite amount of source
     */
    default boolean isInfinite() {
        return false;
    }
}
