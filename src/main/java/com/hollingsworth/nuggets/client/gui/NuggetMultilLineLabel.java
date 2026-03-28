package com.hollingsworth.nuggets.client.gui;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Extended MultiLineLabel that adds no-shadow rendering methods and custom string tracking.
 * In 1.21.11 MultiLineLabel only has visitLines/getLineCount/getWidth; all rendering methods
 * were removed from the interface, so we implement them here directly.
 */
public interface NuggetMultilLineLabel extends MultiLineLabel {
    NuggetMultilLineLabel EMPTY = new NuggetMultilLineLabel() {
        @Override
        public int visitLines(TextAlignment alignment, int x, int y, int lineHeight, ActiveTextCollector collector) {
            return y;
        }

        @Override
        public int getLineCount() {
            return 0;
        }

        @Override
        public int getWidth() {
            return 0;
        }

        @Override
        public void renderCenteredNoShadow(GuiGraphics graphics, int x, int y, int lineHeight) {}

        @Override
        public void renderCenteredNoShadow(GuiGraphics graphics, int x, int y, int lineHeight, int color) {}

        @Override
        public void renderLeftAlignedNoShadow(GuiGraphics graphics, int x, int y, int lineHeight, int color) {}

        @Override
        public String getString() {
            return "";
        }
    };

    // Custom methods not in vanilla MultiLineLabel
    void renderCenteredNoShadow(GuiGraphics graphics, int x, int y, int lineHeight);
    void renderCenteredNoShadow(GuiGraphics graphics, int x, int y, int lineHeight, int color);
    void renderLeftAlignedNoShadow(GuiGraphics graphics, int x, int y, int lineHeight, int color);
    String getString();

    static NuggetMultilLineLabel create(Font font, Component... components) {
        return create(font, Integer.MAX_VALUE, Integer.MAX_VALUE, components);
    }

    static NuggetMultilLineLabel create(Font font, int maxWidth, Component... components) {
        return create(font, maxWidth, Integer.MAX_VALUE, components);
    }

    static NuggetMultilLineLabel create(Font font, Component component, int maxWidth) {
        return create(font, maxWidth, Integer.MAX_VALUE, component);
    }

    static NuggetMultilLineLabel create(final Font font, final int maxWidth, final int maxRows, final Component... components) {
        return components.length == 0 ? EMPTY : new NuggetMultilLineLabel() {
            @Nullable
            private List<MultiLineLabel.TextAndWidth> cachedTextAndWidth;
            @Nullable
            private Language splitWithLanguage;
            private String cachedString;

            @Override
            public int visitLines(TextAlignment alignment, int x, int y, int lineHeight, ActiveTextCollector collector) {
                int currentY = y;
                for (MultiLineLabel.TextAndWidth line : getSplitMessage()) {
                    // calculateLeft converts center/right x to actual draw x based on text width
                    int drawX = alignment.calculateLeft(x, line.width());
                    collector.accept(drawX, currentY, line.text());
                    currentY += lineHeight;
                }
                return currentY;
            }

            @Override
            public void renderCenteredNoShadow(GuiGraphics graphics, int x, int y, int lineHeight) {
                renderCenteredNoShadow(graphics, x, y, lineHeight, -1);
            }

            @Override
            public void renderCenteredNoShadow(GuiGraphics graphics, int x, int y, int lineHeight, int color) {
                // GuiHelpers.drawCenteredStringNoShadow already handles the opaque fix
                int currentY = y;
                for (MultiLineLabel.TextAndWidth line : getSplitMessage()) {
                    GuiHelpers.drawCenteredStringNoShadow(font, graphics, line.text(), x, currentY, color);
                    currentY += lineHeight;
                }
            }

            @Override
            public void renderLeftAlignedNoShadow(GuiGraphics graphics, int x, int y, int lineHeight, int color) {
                // 1.21.11: drawString skips if alpha==0. Color 0 was opaque black in 1.21.1; ensure opaque.
                int c = (color & 0xFF000000) == 0 ? (color | 0xFF000000) : color;
                int currentY = y;
                for (MultiLineLabel.TextAndWidth line : getSplitMessage()) {
                    graphics.drawString(font, line.text(), x, currentY, c, false);
                    currentY += lineHeight;
                }
            }

            @Override
            public String getString() {
                if (cachedString == null) {
                    StringBuilder sb = new StringBuilder();
                    for (Component component : components) {
                        sb.append(" ").append(component.getString());
                    }
                    cachedString = sb.toString().trim();
                }
                return cachedString;
            }

            @Override
            public int getLineCount() {
                return getSplitMessage().size();
            }

            @Override
            public int getWidth() {
                return Math.min(maxWidth, getSplitMessage().stream().mapToInt(MultiLineLabel.TextAndWidth::width).max().orElse(0));
            }

            private List<MultiLineLabel.TextAndWidth> getSplitMessage() {
                Language language = Language.getInstance();
                if (cachedTextAndWidth != null && language == splitWithLanguage) {
                    return cachedTextAndWidth;
                }
                splitWithLanguage = language;
                List<FormattedCharSequence> list = new ArrayList<>();
                for (Component component : components) {
                    list.addAll(font.split(component, maxWidth));
                }
                cachedTextAndWidth = new ArrayList<>();
                for (FormattedCharSequence seq : list.subList(0, Math.min(list.size(), maxRows))) {
                    cachedTextAndWidth.add(new MultiLineLabel.TextAndWidth(seq, font.width(seq)));
                }
                return cachedTextAndWidth;
            }
        };
    }
}
