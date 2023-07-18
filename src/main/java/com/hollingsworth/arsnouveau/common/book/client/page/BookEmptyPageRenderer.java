/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client.page;

import com.klikli_dev.modonomicon.book.page.BookEmptyPage;
import net.minecraft.client.gui.GuiGraphics;

public class BookEmptyPageRenderer extends BookPageRenderer<BookEmptyPage>  {
    public BookEmptyPageRenderer(BookEmptyPage page) {
        super(page);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks) {

    }
}
