package com.hollingsworth.nuggets.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/**
 * Stub — previously a nuggets library class with copy/paste behavior.
 * In this migration, it is an alias for EditBox.
 * NoShadowTextField extends this and overrides rendering directly, so the class just needs to exist.
 */
public class CopyEditBox extends EditBox {

    public CopyEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
    }

    public CopyEditBox(Font font, int x, int y, int width, int height, EditBox previous, Component message) {
        super(font, x, y, width, height, previous, message);
    }
}
