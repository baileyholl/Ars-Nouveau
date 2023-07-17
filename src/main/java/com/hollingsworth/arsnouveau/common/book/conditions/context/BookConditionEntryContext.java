/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.conditions.context;

import com.hollingsworth.arsnouveau.common.book.Book;
import com.hollingsworth.arsnouveau.common.book.BookEntry;

public class BookConditionEntryContext extends BookConditionContext {
    public final BookEntry entry;

    public BookConditionEntryContext(Book book, BookEntry entry) {
        super(book);
        this.entry = entry;
    }

    @Override
    public String toString() {
        return "BookConditionEntryContext{" +
                "book=" + this.book +
                ", entry=" + this.entry +
                '}';
    }

    public BookEntry getEntry() {
        return this.entry;
    }
}
