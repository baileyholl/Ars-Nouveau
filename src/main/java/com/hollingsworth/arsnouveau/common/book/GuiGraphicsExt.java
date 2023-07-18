/*
 * SPDX-FileCopyrightText: 2023 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class GuiGraphicsExt {


    /**
     * Copy of {@link GuiGraphics#drawString(Font, String, float, float, int, boolean)} for Component rendering.
     */
    public static int drawString(GuiGraphics guiGraphics, Font font, @Nullable Component component, float x, float y, int color, boolean drawShadow) {
        if (component == null) {
            return 0;
        } else {
            int i = font.drawInBatch(component, (float)x, (float)y, color, drawShadow, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
            guiGraphics.flushIfUnmanaged();
            return i;
        }
    }
}
