/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.markdown;

import net.minecraft.network.chat.contents.TranslatableContents;
import org.commonmark.internal.renderer.text.ListHolder;

public class ListItemContents extends TranslatableContents {

    private final ListHolder listHolder;

    public ListItemContents(ListHolder listHolder, String pKey) {
        this(listHolder, pKey, TranslatableContents.NO_ARGS);
    }

    public ListItemContents(ListHolder listHolder, String pKey, Object... pArgs) {
        super(pKey, null, pArgs);
        this.listHolder = listHolder;
    }

    public ListHolder getListHolder() {
        return this.listHolder;
    }
}
