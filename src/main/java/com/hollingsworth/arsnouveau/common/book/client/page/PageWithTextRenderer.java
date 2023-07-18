/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client.page;

public interface PageWithTextRenderer {
    /**
     * Gets the x-coordinate where text starts on this page. Use this to handle shifting down text below other content
     * such as title.
     */
    int getTextY();
}
