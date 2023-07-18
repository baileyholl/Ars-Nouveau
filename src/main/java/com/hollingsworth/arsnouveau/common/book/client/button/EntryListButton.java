/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */


package com.hollingsworth.arsnouveau.common.book.client.button;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.book.BookEntry;
import com.hollingsworth.arsnouveau.common.book.BookUnlockCapability;
import com.hollingsworth.arsnouveau.common.book.ModonomiconConstants;
import com.hollingsworth.arsnouveau.common.book.client.BookContentScreen;
import com.hollingsworth.arsnouveau.common.book.client.BookSearchScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class EntryListButton extends Button {

    private static final int ANIM_TIME = 5;

    private final BookSearchScreen parent;
    private final BookEntry entry;
    private float timeHovered;

    public EntryListButton(BookSearchScreen parent, BookEntry entry, int pX, int pY, OnPress pOnPress) {
        super(pX, pY, BookContentScreen.PAGE_WIDTH, 10, Component.translatable(entry.getName()), pOnPress, Button.DEFAULT_NARRATION);

        this.parent = parent;
        this.entry = entry;
    }

    public BookEntry getEntry() {
        return this.entry;
    }

    private int getEntryColor() {
        return 0x000000;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.active) {
            if (this.isHovered()) {
                this.timeHovered = Math.min(ANIM_TIME, this.timeHovered + ClientInfo.deltaTicks);
            } else {
                this.timeHovered = Math.max(0, this.timeHovered - ClientInfo.deltaTicks);
            }

            float time = Math.max(0, Math.min(ANIM_TIME, this.timeHovered + (this.isHovered() ? partialTicks : -partialTicks)));
            float widthFract = time / ANIM_TIME;
            boolean locked = !BookUnlockCapability.isUnlockedFor(Minecraft.getInstance().player, this.entry);

            guiGraphics.pose().scale(0.5F, 0.5F, 0.5F);
            guiGraphics.fill(this.getX() * 2, this.getY() * 2, (this.getX() + (int) ((float) this.width * widthFract)) * 2, (this.getY() + this.height) * 2, 0x22000000);
            RenderSystem.enableBlend();

            if (locked) {
                RenderSystem.setShaderColor(1F, 1F, 1F, 0.7F);
                BookContentScreen.drawLock(guiGraphics, this.parent.getParentScreen().getBook(), this.getX() * 2 + 2, this.getY() * 2 + 2);
            } else {
                this.entry.getIcon().render(guiGraphics, this.getX() * 2 + 2, this.getY() * 2 + 2);
            }

            guiGraphics.pose().scale(2F, 2F, 2F);

            MutableComponent name;
            if (locked) {
                name = Component.translatable(ModonomiconConstants.I18n.Gui.SEARCH_ENTRY_LOCKED);
            } else {
                name = Component.translatable(this.entry.getName());
            }

            //TODO: if we ever add a font style setting to the book, use it here
            guiGraphics.drawString(Minecraft.getInstance().font, name, this.getX() + 12, this.getY(), this.getEntryColor(), false);
        }
    }

    @Override
    public void playDownSound(SoundManager soundHandlerIn) {
        if (this.entry != null) {
            //TODO: play flip sound
        }
    }


}
