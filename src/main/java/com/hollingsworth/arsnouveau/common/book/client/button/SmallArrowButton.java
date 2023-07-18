/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client.button;

import com.hollingsworth.arsnouveau.common.book.ModonomiconConstants;
import com.hollingsworth.arsnouveau.common.book.client.BookContentScreen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class SmallArrowButton extends BookButton {

    public static final int U = 380;
    public static final int V = 12;
    public static final int HEIGHT = 7;
    public static final int WIDTH = 5;

    public final boolean left;

    public SmallArrowButton(BookContentScreen parent, int x, int y, boolean left, Supplier<Boolean> displayCondition, OnPress onPress) {
        super(parent, x, y, U, left ? V + HEIGHT : V, WIDTH, HEIGHT, displayCondition,
                Component.translatable(left ? ModonomiconConstants.I18n.Gui.BUTTON_PREVIOUS : ModonomiconConstants.I18n.Gui.BUTTON_NEXT),
                onPress,
                Component.translatable(left ? ModonomiconConstants.I18n.Gui.BUTTON_PREVIOUS : ModonomiconConstants.I18n.Gui.BUTTON_NEXT)
                //button title equals hover text
        );
        this.left = left;
    }
}
