/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client.button;

import com.hollingsworth.arsnouveau.common.book.ModonomiconConstants;
import com.hollingsworth.arsnouveau.common.book.client.BookContentScreen;
import net.minecraft.network.chat.Component;

public class BackButton extends BookButton {

    public static final int U = 380;
    public static final int V = 0;
    public static final int HEIGHT = 9;
    public static final int WIDTH = 18;

    public BackButton(BookContentScreen parent, int x, int y) {
        super(parent, x, y, U, V, WIDTH, HEIGHT, parent::canSeeBackButton,
                Component.translatable(ModonomiconConstants.I18n.Gui.BUTTON_BACK),
                parent::handleBackButton,
                Component.translatable(ModonomiconConstants.I18n.Gui.BUTTON_BACK_TOOLTIP)
        );
    }
}
