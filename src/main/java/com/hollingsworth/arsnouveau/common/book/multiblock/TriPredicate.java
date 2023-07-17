/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */
package com.hollingsworth.arsnouveau.common.book.multiblock;

@FunctionalInterface
public interface TriPredicate<A, B, C> {
    boolean test(A a, B b, C c);
}
