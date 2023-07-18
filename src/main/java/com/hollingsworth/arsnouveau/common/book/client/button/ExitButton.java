/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client.button;

import com.hollingsworth.arsnouveau.common.book.ModonomiconConstants;
import com.hollingsworth.arsnouveau.common.book.client.BookScreenWithButtons;
import net.minecraft.network.chat.Component;


public class ExitButton extends BookButton {

    public static final int U = 350;
    public static final int V = 0;
    public static final int HEIGHT = 12;
    public static final int WIDTH = 12;

    public ExitButton(BookScreenWithButtons parent, int x, int y, OnPress onPress) {
        super(parent, x, y, U, V, WIDTH, HEIGHT, () -> true,
                Component.translatable(ModonomiconConstants.I18n.Gui.BUTTON_EXIT),
                onPress,
                Component.translatable(ModonomiconConstants.I18n.Gui.BUTTON_EXIT) //button title equals hover text
        );
    }
}
