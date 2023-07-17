/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.jetbrains.annotations.NotNull;

public class BookTextHolder {

    public static final BookTextHolder EMPTY = new BookTextHolder("");

    private Component component;
    private String string;

    protected BookTextHolder() {
    }

    public BookTextHolder(Component component) {
        this.component = component;
    }

    public BookTextHolder(@NotNull String string) {
        this.string = string;
    }

    public static BookTextHolder fromNetwork(FriendlyByteBuf buffer) {
        if (buffer.readBoolean()) {
            return new BookTextHolder(buffer.readComponent());
        } else {
            return new BookTextHolder(buffer.readUtf());
        }
    }

    public String getString() {
        return this.hasComponent() ? this.component.getString() : I18n.get(this.string);
    }

    /**
     * Gets the translation key, or null if none
     */
    public String getKey() {
        if (this.hasComponent() && this.component.getContents() instanceof TranslatableContents contents) {
            return contents.getKey();
        }
        return this.string;
    }

    public Component getComponent() {
        return this.component;
    }

    public boolean hasComponent() {
        return this.component != null;
    }
    public boolean isEmpty() {
        //Note: BookTextHolder needs to override this, because string will always be null for it
        return (this.hasComponent() ? this.component.getString() : this.string).isEmpty();
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.hasComponent());
        if (this.hasComponent()) {
            buffer.writeComponent(this.component);
        } else {
            buffer.writeUtf(this.string);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        var that = (BookTextHolder) o;

        if(this.hasComponent() && that.hasComponent()){
            return this.component.equals(that.component);
        }

        if(this.string != null)
            return this.string.equals(that.string);

        return false;
    }
}
