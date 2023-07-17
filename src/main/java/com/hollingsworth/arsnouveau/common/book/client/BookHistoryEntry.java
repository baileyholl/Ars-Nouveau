/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client;

import net.minecraft.resources.ResourceLocation;

public class BookHistoryEntry {
    public ResourceLocation bookId;
    public ResourceLocation categoryId;
    public ResourceLocation entryId;
    public int page;

    public BookHistoryEntry(ResourceLocation bookId, ResourceLocation categoryId, ResourceLocation entryId, int page) {
        this.bookId = bookId;
        this.categoryId = categoryId;
        this.entryId = entryId;
        this.page = page;
    }
}
