/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.conditions.context;

import com.hollingsworth.arsnouveau.common.book.Book;
import com.hollingsworth.arsnouveau.common.book.BookCategory;
import com.hollingsworth.arsnouveau.common.book.BookEntry;

public abstract class BookConditionContext {
    public final Book book;

    public BookConditionContext(Book book) {
        this.book = book;
    }

    public Book getBook() {
        return this.book;
    }

    @Override
    public String toString() {
        return "BookConditionContext{" +
                "book=" + this.book +
                '}';
    }

    public static BookConditionContext of(Book book, BookCategory category) {
        return new BookConditionCategoryContext(book, category);
    }

    public static BookConditionContext of(Book book, BookEntry entry) {
        return new BookConditionEntryContext(book, entry);
    }
}
