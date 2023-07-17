/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.conditions.context;

import com.hollingsworth.arsnouveau.common.book.Book;
import com.hollingsworth.arsnouveau.common.book.BookCategory;

public class BookConditionCategoryContext extends BookConditionContext {
    public final BookCategory category;

    public BookConditionCategoryContext(Book book, BookCategory category) {
        super(book);
        this.category = category;
    }

    @Override
    public String toString() {
        return "BookConditionCategoryContext{" +
                "book=" + this.book +
                ", category=" + this.category +
                '}';
    }

    public BookCategory getCategory() {
        return this.category;
    }
}
