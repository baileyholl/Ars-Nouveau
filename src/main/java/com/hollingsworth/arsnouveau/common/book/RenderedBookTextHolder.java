/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class RenderedBookTextHolder extends BookTextHolder {

    private final BookTextHolder original;
    private final List<MutableComponent> renderedText;

    public RenderedBookTextHolder(BookTextHolder original, List<MutableComponent> renderedText) {
        this.original = original;
        this.renderedText = renderedText;
    }

    public List<MutableComponent> getRenderedText() {
        return this.renderedText;
    }

    @Override
    public String getString() {
        return this.original.getString();
    }

    @Override
    public Component getComponent() {
        return this.original.getComponent();
    }

    @Override
    public boolean isEmpty() {
        return this.getRenderedText().isEmpty();
    }

    @Override
    public boolean hasComponent() {
        return this.original.hasComponent();
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        this.original.toNetwork(buffer);
    }
}
