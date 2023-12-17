package com.hollingsworth.arsnouveau.client.gui.book;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class GlyphNode {
    @Nullable
    private GlyphNode parent;
    private final List<GlyphNode> children = Lists.newArrayList();
    public int x;
    public int y;
    public ItemStack renderStack;
    public GlyphNode(GlyphNode pParent, int pX, int pY, ItemStack stack) {
        this.parent = pParent;
        this.x = pX;
        this.y = pY;
        this.renderStack = stack;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void addChild(GlyphNode pAdvancementWidget) {
        this.children.add(pAdvancementWidget);
    }

    public void draw(GuiGraphics pGuiGraphics, int pX, int pY) {
        pGuiGraphics.renderFakeItem(renderStack, pX + this.x + 8, pY + this.y + 5);

        for(GlyphNode advancementwidget : this.children) {
            advancementwidget.draw(pGuiGraphics, pX, pY);
        }
    }


    public void drawConnectivity(GuiGraphics pGuiGraphics, int pX, int pY, boolean pDropShadow) {
        if (this.parent != null) {
            int i = pX + this.parent.x + 13;
            int j = pX + this.parent.x + 26 + 4;
            int k = pY + this.parent.y + 13;
            int l = pX + this.x + 13;
            int i1 = pY + this.y + 13;
            int j1 = pDropShadow ? -16777216 : -1;
            if (pDropShadow) {
                pGuiGraphics.hLine(j, i, k - 1, j1);
                pGuiGraphics.hLine(j + 1, i, k, j1);
                pGuiGraphics.hLine(j, i, k + 1, j1);
                pGuiGraphics.hLine(l, j - 1, i1 - 1, j1);
                pGuiGraphics.hLine(l, j - 1, i1, j1);
                pGuiGraphics.hLine(l, j - 1, i1 + 1, j1);
                pGuiGraphics.vLine(j - 1, i1, k, j1);
                pGuiGraphics.vLine(j + 1, i1, k, j1);
            } else {
                pGuiGraphics.hLine(j, i, k, j1);
                pGuiGraphics.hLine(l, j, i1, j1);
                pGuiGraphics.vLine(j, i1, k, j1);
            }
        }

        for(GlyphNode advancementwidget : this.children) {
            advancementwidget.drawConnectivity(pGuiGraphics, pX, pY, pDropShadow);
        }

    }
}
