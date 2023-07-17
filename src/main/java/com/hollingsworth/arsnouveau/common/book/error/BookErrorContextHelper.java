/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.error;

import net.minecraft.resources.ResourceLocation;

public class BookErrorContextHelper {
    public ResourceLocation categoryId;
    public ResourceLocation entryId;
    public int pageNumber = 1;

    public void reset() {
        this.categoryId = null;
        this.entryId = null;
        this.pageNumber = -1;
    }

    @Override
    public String toString() {
        var categoryId = this.categoryId == null ? "null" : this.categoryId.toString();
        var entryId = this.entryId == null ? "null" : this.entryId.toString();
        var pageNumber = this.pageNumber == -1 ? "null" : this.pageNumber;
        return "Category: " + categoryId + ", \nEntry: " + entryId + ", \nPage: " + pageNumber;
    }
}
