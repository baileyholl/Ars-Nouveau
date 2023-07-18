/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 * SPDX-FileCopyrightText: 2021 Authors of Arcana
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.client;

import com.hollingsworth.arsnouveau.common.book.BookEntry;
import com.hollingsworth.arsnouveau.common.book.BookEntryParent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import static java.lang.Math.*;

public class EntryConnectionRenderer {

    public int blitOffset;
    public ResourceLocation entryTextures;

    public EntryConnectionRenderer(ResourceLocation entryTextures) {
        this.entryTextures = entryTextures;
    }

    public void renderLinedUpEntries(GuiGraphics guiGraphics, BookEntry entry, BookEntry parentEntry, BookEntryParent parent, boolean isVertical) {
        if (isVertical) {
            this.drawVerticalLine(guiGraphics, parentEntry.getX(), entry.getY(), parentEntry.getY());
            if (parent.drawArrow()) {
                //move the arrow head one grid slot before the target, because it occupies 30x30
                if (parentEntry.getY() > entry.getY())
                    this.drawUpArrow(guiGraphics, entry.getX(), entry.getY() + 1);
                else
                    this.drawDownArrow(guiGraphics, entry.getX(), entry.getY() - 1);
            }

        } else {
            this.drawHorizontalLine(guiGraphics, parentEntry.getY(), entry.getX(), parentEntry.getX());
            if (parent.drawArrow()) {
                //move the arrow head one grid slot before the target, because it occupies 30x30
                if (parentEntry.getX() > entry.getX())
                    this.drawLeftArrow(guiGraphics, entry.getX() + 1, entry.getY());
                else
                    this.drawRightArrow(guiGraphics, entry.getX() - 1, entry.getY());
            }
        }
    }

    public void renderSmallCurves(GuiGraphics guiGraphics, BookEntry entry, BookEntry parentEntry, BookEntryParent parent) {
        this.drawVerticalLine(guiGraphics, entry.getX(), parentEntry.getY(), entry.getY());
        this.drawHorizontalLine(guiGraphics, parentEntry.getY(), parentEntry.getX(), entry.getX());
        if (entry.getX() > parentEntry.getX()) {
            if (entry.getY() > parentEntry.getY()) {
                this.drawSmallCurveLeftDown(guiGraphics, entry.getX(), parentEntry.getY());
                if (parent.drawArrow())
                    this.drawDownArrow(guiGraphics, entry.getX(), entry.getY() - 1);
            } else {
                this.drawSmallCurveLeftUp(guiGraphics, entry.getX(), parentEntry.getY());
                if (parent.drawArrow())
                    this.drawUpArrow(guiGraphics, entry.getX(), entry.getY() + 1);
            }
        } else {
            if (entry.getY() > parentEntry.getY()) {
                this.drawSmallCurveRightDown(guiGraphics, entry.getX(), parentEntry.getY());
                if (parent.drawArrow())
                    this.drawDownArrow(guiGraphics, entry.getX(), entry.getY() - 1);
            } else {
                this.drawSmallCurveRightUp(guiGraphics, entry.getX(), parentEntry.getY());
                if (parent.drawArrow())
                    this.drawUpArrow(guiGraphics, entry.getX(), entry.getY() + 1);
            }
        }
    }

    public void renderSmallCurvesReversed(GuiGraphics guiGraphics, BookEntry entry, BookEntry parentEntry, BookEntryParent parent) {
        this.drawHorizontalLine(guiGraphics, entry.getY(), entry.getX(), parentEntry.getX());
        this.drawVerticalLine(guiGraphics, parentEntry.getX(), parentEntry.getY(), entry.getY());
        if (entry.getX() > parentEntry.getX()) {
            if (entry.getY() > parentEntry.getY()) {
                this.drawSmallCurveRightUp(guiGraphics, parentEntry.getX(), entry.getY());
                if (parent.drawArrow())
                    this.drawRightArrow(guiGraphics, entry.getX() - 1, entry.getY());
            } else {
                this.drawSmallCurveRightDown(guiGraphics, entry.getX() - 1, parentEntry.getY() - 1);
                if (parent.drawArrow())
                    this.drawRightArrow(guiGraphics, entry.getX() - 1, entry.getY());
            }
        } else {
            if (entry.getY() > parentEntry.getY()) {
                this.drawSmallCurveLeftUp(guiGraphics, entry.getX() + 1, entry.getY());
                if (parent.drawArrow())
                    this.drawLeftArrow(guiGraphics, entry.getX() + 1, entry.getY());
            } else {
                this.drawSmallCurveLeftDown(guiGraphics, entry.getX() + 1, parentEntry.getY() - 1);
                if (parent.drawArrow())
                    this.drawLeftArrow(guiGraphics, entry.getX() + 1, entry.getY());
            }
        }
    }

    public void renderLargeCurves(GuiGraphics guiGraphics, BookEntry entry, BookEntry parentEntry, BookEntryParent parent) {
        this.drawHorizontalLineShortened(guiGraphics, parentEntry.getY(), parentEntry.getX(), entry.getX());
        this.drawVerticalLineShortened(guiGraphics, entry.getX(), entry.getY(), parentEntry.getY());
        if (entry.getX() > parentEntry.getX()) {
            if (entry.getY() > parentEntry.getY()) {
                this.drawLargeCurveLeftDown(guiGraphics, entry.getX() - 1, parentEntry.getY());
                if (parent.drawArrow())
                    this.drawDownArrow(guiGraphics, entry.getX(), entry.getY() - 1);
            } else {
                this.drawLargeCurveLeftUp(guiGraphics, entry.getX() - 1, parentEntry.getY() - 1);
                if (parent.drawArrow())
                    this.drawUpArrow(guiGraphics, entry.getX(), entry.getY() + 1);
            }
        } else {
            if (entry.getY() > parentEntry.getY()) {
                this.drawLargeCurveRightDown(guiGraphics, entry.getX(), parentEntry.getY());
                if (parent.drawArrow())
                    this.drawDownArrow(guiGraphics, entry.getX(), entry.getY() - 1);
            } else {
                this.drawLargeCurveRightUp(guiGraphics, entry.getX(), parentEntry.getY() - 1);
                if (parent.drawArrow())
                    this.drawUpArrow(guiGraphics, entry.getX(), entry.getY() + 1);
            }
        }
    }

    public void renderLargeCurvesReversed(GuiGraphics guiGraphics, BookEntry entry, BookEntry parentEntry, BookEntryParent parent) {
        this.drawHorizontalLineShortened(guiGraphics, entry.getY(), entry.getX(), parentEntry.getX());
        this.drawVerticalLineShortened(guiGraphics, parentEntry.getX(), parentEntry.getY(), entry.getY());
        if (entry.getX() > parentEntry.getX()) {
            if (entry.getY() > parentEntry.getY())
                this.drawLargeCurveRightUp(guiGraphics, parentEntry.getX(), entry.getY() - 1);
            else
                this.drawLargeCurveRightDown(guiGraphics, parentEntry.getX(), entry.getY());
            if (parent.drawArrow())
                this.drawRightArrow(guiGraphics, entry.getX() - 1, entry.getY());
        } else {
            if (entry.getY() > parentEntry.getY())
                this.drawLargeCurveLeftUp(guiGraphics, entry.getX() + 1, entry.getY() - 1);
            else
                this.drawLargeCurveLeftDown(guiGraphics, entry.getX() + 1, entry.getY());
            if (parent.drawArrow())
                this.drawLeftArrow(guiGraphics, entry.getX() + 1, entry.getY());
        }
    }

    public void render(GuiGraphics guiGraphics, BookEntry entry, BookEntryParent parent) {
        BookEntry parentEntry = parent.getEntry();

        //only render if line is enabled and if we are in the same category (other category -> other page!)
        if (parent.isLineEnabled() && parentEntry.getCategory().equals(entry.getCategory())) {
            int deltaX = abs(entry.getX() - parentEntry.getX());
            int deltaY = abs(entry.getY() - parentEntry.getY());

            if (deltaX == 0 || deltaY == 0) {
                //if the entries are in a line, just draw a line
                this.renderLinedUpEntries(guiGraphics, entry, parentEntry, parent, deltaX == 0);
            } else {
                if (deltaX < 2 || deltaY < 2) {
                    if (!parent.isLineReversed()) {
                        this.renderSmallCurves(guiGraphics, entry, parentEntry, parent);
                    } else {
                        this.renderSmallCurvesReversed(guiGraphics, entry, parentEntry, parent);
                    }
                } else {
                    if (!parent.isLineReversed()) {
                        this.renderLargeCurves(guiGraphics, entry, parentEntry, parent);
                    } else {
                        this.renderLargeCurvesReversed(guiGraphics, entry, parentEntry, parent);
                    }
                }
            }
        }
    }

    protected void setBlitOffset(int blitOffset) {
        this.blitOffset = blitOffset;
    }

    /**
     * Scales from grid coordinates (1, 2, 3, ... ) to screen coordinates (30, 60, 90)
     */
    protected int screenX(int x) {
        return x * BookCategoryScreen.ENTRY_GRID_SCALE;
    }

    /**
     * Scales from grid coordinates (1, 2, 3, ... ) to screen coordinates (30, 60, 90)
     */
    protected int screenY(int y) {
        return y * BookCategoryScreen.ENTRY_GRID_SCALE;
    }

    protected void blit(GuiGraphics guiGraphics, int pX, int pY, float pUOffset, float pVOffset, int pUWidth, int pVHeight) {
        guiGraphics.blit(this.entryTextures, pX, pY, this.blitOffset, pUOffset, pVOffset, pUWidth, pVHeight, 256, 256);
    }

    protected void drawSmallCurveLeftDown(GuiGraphics guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.screenX(x), this.screenY(y), 0, 226, 30, 30);
    }

    protected void drawSmallCurveRightDown(GuiGraphics guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.screenX(x), this.screenY(y), 30, 226, 30, 30);
    }

    protected void drawSmallCurveLeftUp(GuiGraphics guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.screenX(x), this.screenY(y), 0, 196, 30, 30);
    }

    protected void drawSmallCurveRightUp(GuiGraphics guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.screenX(x), this.screenY(y), 30, 196, 30, 30);
    }

    protected void drawLargeCurveLeftDown(GuiGraphics guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.screenX(x), this.screenY(y), 62, 196, 60, 60);
    }

    protected void drawLargeCurveRightDown(GuiGraphics guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.screenX(x), this.screenY(y), 122, 196, 60, 60);
    }

    protected void drawLargeCurveLeftUp(GuiGraphics guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.screenX(x), this.screenY(y), 62, 134, 60, 60);
    }

    protected void drawLargeCurveRightUp(GuiGraphics guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.screenX(x), this.screenY(y), 122, 134, 60, 60);
    }

    void drawVerticalLineAt(GuiGraphics guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.screenX(x), this.screenY(y), 184, 164, 30, 31);
    }

    void drawHorizontalLineAt(GuiGraphics guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.screenX(x), this.screenY(y), 184, 226, 31, 30);
    }

    void drawVerticalLine(GuiGraphics guiGraphics, int x, int startY, int endY) {
        int temp = startY;

        //swap them if endY > startY
        startY = min(startY, endY);
        endY = max(endY, temp);

        for (int j = startY + 1; j < endY; j++)
            this.drawVerticalLineAt(guiGraphics, x, j);
    }

    void drawHorizontalLine(GuiGraphics guiGraphics, int y, int startX, int endX) {
        int temp = startX;

        //swap them if endX > startX
        startX = min(startX, endX);
        endX = max(endX, temp);
        // *exclusive*
        for (int j = startX + 1; j < endX; j++) {
            this.drawHorizontalLineAt(guiGraphics, j, y);
        }
    }

    void drawHorizontalLineShortened(GuiGraphics guiGraphics, int y, int startX, int endX) {
        int temp = startX;

        // reduce length by one
        if (startX > endX)
            endX++;
        else
            endX--;

        //swap them if endX > startX
        startX = min(startX, endX);
        endX = max(endX, temp);

        for (int j = startX + 1; j < endX; j++)
            this.drawHorizontalLineAt(guiGraphics, j, y);
    }

    void drawVerticalLineShortened(GuiGraphics guiGraphics, int x, int startY, int endY) {
        int temp = startY;

        // reduce length by one
        if (startY > endY)
            endY++;
        else
            endY--;

        //swap them if endY > startY
        startY = min(startY, endY);
        endY = max(endY, temp);

        for (int j = startY + 1; j < endY; j++)
            this.drawVerticalLineAt(guiGraphics, x, j);
    }


    void drawUpArrow(GuiGraphics guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.screenX(x), this.screenY(y) - 1, 0, 134, 30, 30);
    }

    void drawDownArrow(GuiGraphics guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.screenX(x), this.screenY(y) + 1, 0, 164, 30, 30);
    }

    void drawRightArrow(GuiGraphics guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.screenX(x) + 1, this.screenY(y), 30, 134, 30, 30);
    }

    void drawLeftArrow(GuiGraphics guiGraphics, int x, int y) {
        this.blit(guiGraphics, this.screenX(x) - 1, this.screenY(y), 30, 164, 30, 30);
    }
}
