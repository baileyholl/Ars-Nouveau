/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book;

public class ResolvedBookEntryParent extends BookEntryParent {
    protected BookEntry entry;

    public ResolvedBookEntryParent(BookEntry entry) {
        super(entry.getId());
        this.entry = entry;
    }

    @Override
    public BookEntry getEntry() {
        return this.entry;
    }
}
