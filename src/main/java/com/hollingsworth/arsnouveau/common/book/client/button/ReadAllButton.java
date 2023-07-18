/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client.button;

import com.hollingsworth.arsnouveau.common.book.ModonomiconConstants;
import com.hollingsworth.arsnouveau.common.book.ModonomiconConstants.I18n.Gui;
import com.hollingsworth.arsnouveau.common.book.client.BookOverviewScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

public class ReadAllButton extends Button {

    public static final int U = 0;
    public static final int V_READ_UNLOCKED = 196;
    public static final int V_READ_ALL = 210;
    public static final int V_NONE = 224;
    public static final int WIDTH = 16;
    public static final int HEIGHT = 14;


    private final BookOverviewScreen parent;

    private final MutableComponent tooltipReadUnlocked;
    private final MutableComponent tooltipReadAll;
    private final MutableComponent tooltipNone;
    private final MutableComponent tooltipShiftInstructions;
    private final MutableComponent tooltipShiftWarning;
    private final Supplier<Boolean> displayCondition;
    private final Supplier<Boolean> hasUnreadUnlockedEntries;

    private boolean wasHovered;
    private int tooltipMsDelay;
    private long hoveredStartTime;


    public ReadAllButton(BookOverviewScreen parent, int x, int y, Supplier<Boolean> hasUnreadUnlockedEntries, Supplier<Boolean> displayCondition, OnPress onPress) {
        super(x, y, WIDTH, HEIGHT,
                Component.translatable(ModonomiconConstants.I18n.Gui.BUTTON_READ_ALL),
                onPress, Button.DEFAULT_NARRATION
        );
        this.parent = parent;
        this.tooltipReadUnlocked = Component.translatable(Gui.BUTTON_READ_ALL_TOOLTIP_READ_UNLOCKED);
        this.tooltipReadAll = Component.translatable(Gui.BUTTON_READ_ALL_TOOLTIP_READ_ALL);
        this.tooltipNone = Component.translatable(Gui.BUTTON_READ_ALL_TOOLTIP_NONE);
        this.tooltipShiftInstructions = Component.translatable(Gui.BUTTON_READ_ALL_TOOLTIP_SHIFT_INSTRUCTIONS);
        this.tooltipShiftWarning = Component.translatable(Gui.BUTTON_READ_ALL_TOOLTIP_SHIFT_WARNING);
        this.hasUnreadUnlockedEntries = hasUnreadUnlockedEntries;
        this.displayCondition = displayCondition;
    }

    @Override
    public final void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.active = this.visible = this.displayCondition.get();
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.updateCustomTooltip();
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        //if focused we go to the right of our normal button (instead of down, like mc buttons do)

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 200);
        var hovered = this.isHovered();

        int u = U;

        //by default we show green if we can read unlocked entries or gray if none
        //if shift is down we offer to mark all as read
        //if neither is possible the button should be hidden which is handled by BookOverviewScreen#canSeeReadAllButton
        int v = this.hasUnreadUnlockedEntries.get() ? V_READ_UNLOCKED : V_NONE;

        if (hovered)
            u += this.width; //shift to the right for hover variant

        if (Screen.hasShiftDown()) {
            v = V_READ_ALL;
        }

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        guiGraphics.blit(this.parent.getBook().getBookOverviewTexture(), this.getX(), this.getY(), u, v, this.width, this.height, 256, 256);

        guiGraphics.pose().popPose();
    }

    private void updateCustomTooltip() {

        boolean flag = this.isHovered();
        if (flag != this.wasHovered) {
            if (flag) {
                this.hoveredStartTime = Util.getMillis();
            }

            this.wasHovered = flag;
        }

        if (flag && Util.getMillis() - this.hoveredStartTime > (long) this.tooltipMsDelay) {
            var tooltip = this.getCustomTooltip();

            Screen screen = Minecraft.getInstance().screen;
            if (screen != null) {
                screen.setTooltipForNextRenderPass(Tooltip.create(tooltip), this.createTooltipPositioner(), this.isHovered());
            }
        }

    }

    public MutableComponent getCustomTooltip() {

        if (Screen.hasShiftDown()) {
            return Component.empty().append(this.tooltipReadAll).append(Component.literal("\n\n")).append(this.tooltipShiftWarning);
        }

        if (this.hasUnreadUnlockedEntries.get()) {
            return Component.empty().append(this.tooltipReadUnlocked).append(Component.literal("\n\n")).append(this.tooltipShiftInstructions);
        }

        return Component.empty().append(this.tooltipNone).append(Component.literal("\n\n")).append(this.tooltipShiftInstructions);
    }

}
