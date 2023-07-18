/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client.button;

import com.hollingsworth.arsnouveau.common.book.ModonomiconConstants;
import com.hollingsworth.arsnouveau.common.book.client.BookContentScreen;
import net.minecraft.network.chat.Component;

public class VisualizeButton extends BookButton {

    public static final int U = 350;
    public static final int V = 12;
    public static final int HEIGHT = 7;
    public static final int WIDTH = 11;

    public VisualizeButton(BookContentScreen parent, int x, int y, OnPress onPress) {
        super(parent, x, y, U, V, WIDTH, HEIGHT,
                Component.translatable(ModonomiconConstants.I18n.Gui.BUTTON_VISUALIZE),
                onPress,
                Component.translatable(ModonomiconConstants.I18n.Gui.BUTTON_VISUALIZE_TOOLTIP)
        );
    }
}
