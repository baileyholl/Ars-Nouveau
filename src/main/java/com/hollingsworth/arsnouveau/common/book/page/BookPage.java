/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.page;


import com.hollingsworth.arsnouveau.common.book.Book;
import com.hollingsworth.arsnouveau.common.book.BookEntry;
import com.hollingsworth.arsnouveau.common.book.BookTextRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public abstract class BookPage {

    protected Book book;
    protected BookEntry parentEntry;
    protected int pageNumber;

    protected String anchor;

    public BookPage(String anchor) {
        this.anchor = anchor;
    }

    public String getAnchor() {
        return this.anchor;
    }

    public abstract ResourceLocation getType();

    /**
     * call after loading the book jsons to finalize.
     */
    public void build(BookEntry parentEntry, int pageNum) {
        this.parentEntry = parentEntry;
        this.pageNumber = pageNum;
        this.book = this.parentEntry.getBook();
    }

    /**
     * Called after build() (after loading the book jsons) to render markdown and store any errors
     */
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
    }


    public Book getBook() {
        return this.book;
    }

    public abstract void toNetwork(FriendlyByteBuf buffer);


    public BookEntry getParentEntry() {
        return this.parentEntry;
    }

    public void setParentEntry(BookEntry parentEntry) {
        this.parentEntry = parentEntry;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public abstract boolean matchesQuery(String query);
}
