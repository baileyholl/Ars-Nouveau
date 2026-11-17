package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;

// TODO: Remove unused boolean param next breaking change
public record SchoolTooltip(AbstractSpellPart part, boolean glyphItem) implements TooltipComponent {
    public SchoolTooltip(AbstractSpellPart part) {
        this(part, false);
    }

    public static class SchoolTooltipRenderer implements ClientTooltipComponent {

        static int LINE_HEIGHT = 10;
        static int TEXT_INDENT = 12;
        static int ICON_SPACING = 11;
        private final List<SpellSchool> schools;

        public SchoolTooltipRenderer(SchoolTooltip pSchoolTooltip) {
            this(pSchoolTooltip.part());
        }

        public SchoolTooltipRenderer(AbstractSpellPart part) {
            this.schools = part.spellSchools;
        }

        @Override
        public int getHeight() {
            return Screen.hasShiftDown() ? LINE_HEIGHT * schools.size() : LINE_HEIGHT;
        }

        @Override
        public int getWidth(@NotNull Font font) {
            if (!Screen.hasShiftDown()) {
                return ICON_SPACING * schools.size();
            }
            int width = 0;
            for (SpellSchool school : schools) {
                width = Math.max(width, TEXT_INDENT + font.width(school.getTextComponent()));
            }
            return width;
        }

        @Override
        public void renderText(@NotNull Font font, int x, int y, @NotNull Matrix4f matrix, MultiBufferSource.@NotNull BufferSource bufferSource) {
            if (!Screen.hasShiftDown()) {
                return;
            }
            for (SpellSchool school : schools) {
                font.drawInBatch(school.getTextComponent(), x + TEXT_INDENT, y, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                y += LINE_HEIGHT;
            }
        }

        @Override
        public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics guiGraphics) {
            boolean shift = Screen.hasShiftDown();
            for (SpellSchool school : schools) {
                guiGraphics.blit(school.getTexturePath(), x - 2, y - 2, 0, 0, 12, 12, 12, 12);
                if (shift) {
                    y += LINE_HEIGHT;
                } else {
                    x += ICON_SPACING;
                }
            }
        }
    }
}
