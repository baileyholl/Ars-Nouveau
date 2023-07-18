/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.markdown;

import net.minecraft.network.chat.TextColor;

/**
 * @param renderSoftLineBreaks           True to render soft line breaks (deviating from MD spec). Should usually be
 *                                       false.
 * @param replaceSoftLineBreaksWithSpace True to replace soft line breaks with spaces. Should usually be true, prevents
 *                                       IDE line breaks from causing words to be rendered without spaces inbetween.
 * @param linkColor                      The color to use for http and book page links. Suggested: Blue: 0x5555FF
 */
public record ComponentRendererContextRecord(boolean renderSoftLineBreaks, boolean replaceSoftLineBreaksWithSpace,
                                             TextColor linkColor) {
    //TODO: make renderSoftLineBreaks a book level option
}
