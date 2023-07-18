/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.markdown;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class MarkdownComponentRenderUtils {
    /**
     * Adopted from
     * {@link net.minecraft.client.gui.components.ComponentRenderUtils#wrapComponents(FormattedText, int, Font)} Wraps
     * translatable components and gracefully handles markdown lists.
     *
     * @param text      the text to wrap.
     * @param width     the max width of the text to wrap at
     * @param listWidth the alternate width to use for lists. This is a hack to avoid issues with indent for wrapped
     *                  lines causing the lines to exceed the width. Could be e.g. width-10
     * @param font      The font to use, will usually be Minecraft.getInstance().font;
     * @return a list of wrapped lines ready to render via font.
     */
    public static List<FormattedCharSequence> wrapComponents(MutableComponent text, int width, int listWidth, Font font) {
        if (text.getContents() instanceof ListItemContents) {
            width = listWidth;
        }

        List<FormattedCharSequence> list = Lists.newArrayList();
        font.getSplitter().splitLines(text, width, Style.EMPTY, (lineText, isWrapped) -> {
            FormattedCharSequence formattedcharsequence = Language.getInstance().getVisualOrder(lineText);
            var indent = FormattedCharSequence.EMPTY;
            if (text.getContents() instanceof ListItemContents item) {
                indent = FormattedCharSequence.forward(item.getListHolder().getIndent() + "   ", Style.EMPTY);
            }
            list.add(isWrapped ? FormattedCharSequence.composite(indent, formattedcharsequence) : formattedcharsequence);
        });
        return (list.isEmpty() ? Lists.newArrayList(FormattedCharSequence.EMPTY) : list);
    }
}
