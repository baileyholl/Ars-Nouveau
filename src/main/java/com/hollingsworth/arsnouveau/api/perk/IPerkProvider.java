package com.hollingsworth.arsnouveau.api.perk;

/**
 * Returns a perk holder serialized from T
 * You should not implement this interface directly. Use a non-generic child interface.
 */
@FunctionalInterface
public interface IPerkProvider<T> {

    IPerkHolder<T> getPerkHolder(T obj);

}
