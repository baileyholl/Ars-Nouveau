/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client.button;

import com.hollingsworth.arsnouveau.common.book.client.BookContentScreen;
import com.hollingsworth.arsnouveau.common.book.client.BookScreenWithButtons;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;

public class BookButton extends Button {

    protected final BookScreenWithButtons parent;
    protected final int u, v;
    protected final Supplier<Boolean> displayCondition;
    protected final List<Component> tooltip;

    public BookButton(BookScreenWithButtons parent, int x, int y, int u, int v, int w, int h, Component pMessage, OnPress onPress, Component... tooltip) {
        this(parent, x, y, u, v, w, h, () -> true, pMessage, onPress, tooltip);
    }

    public BookButton(BookScreenWithButtons parent, int x, int y, int u, int v, int w, int h, Supplier<Boolean> displayCondition, Component pMessage, OnPress onPress, Component... tooltip) {
        super(x, y, w, h, pMessage, onPress, Button.DEFAULT_NARRATION);
        this.parent = parent;
        this.u = u;
        this.v = v;
        this.displayCondition = displayCondition;
        this.tooltip = List.of(tooltip);
    }

    @Override
    public final void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.active = this.visible = this.displayCondition.get();
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        //if focused we go to the right of our normal button (instead of down, like mc buttons do)
        BookContentScreen.drawFromTexture(guiGraphics, this.parent.getBook(), this.getX(), this.getY(), this.u + (this.isHovered() ? this.width : 0), this.v, this.width, this.height);
        if (this.isHovered()) {
            this.parent.setTooltip(this.tooltip);
        }
    }
}
