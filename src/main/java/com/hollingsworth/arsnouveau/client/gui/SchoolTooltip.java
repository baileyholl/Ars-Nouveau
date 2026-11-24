package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;

// TODO: Remove unused glyphItem next breaking change
public record SchoolTooltip(AbstractSpellPart part, boolean glyphItem,
                            boolean showSpellStyleTooltip) implements TooltipComponent {

    @Deprecated(forRemoval = true)
    public SchoolTooltip(AbstractSpellPart part, boolean glyphItem) {
        this(part, glyphItem, false);
    }

    public SchoolTooltip(AbstractSpellPart part) {
        this(part, false, false);
    }

    @Deprecated(forRemoval = true)
    public List<SpellSchool> schools() {
        return part.spellSchools;
    }

    @Deprecated(forRemoval = true)
    public String name() {
        return glyphItem ? Component.translatable("ars_nouveau.glyph_of", part.getLocaleName()).getString() : part.getLocaleName();
    }


    public static class SchoolTooltipRenderer implements ClientTooltipComponent {

        static int LINE_HEIGHT = 10;
        static int TEXT_INDENT = 12;
        private final List<SpellSchool> schools;
        private final String name;
        public boolean showSpellStyleTooltip = false;

        public SchoolTooltipRenderer(SchoolTooltip pSchoolTooltip) {
            this(pSchoolTooltip.part(), pSchoolTooltip.showSpellStyleTooltip());
        }

        public SchoolTooltipRenderer(AbstractSpellPart part) {
            this(part, false);
        }

        public SchoolTooltipRenderer(AbstractSpellPart part, boolean showSpellStyleTooltip) {
            this.schools = part.spellSchools;
            this.name = part.getLocaleName();
            this.showSpellStyleTooltip = showSpellStyleTooltip;
        }

        @Override
        public int getHeight() {
            if (Screen.hasShiftDown()) {
                return LINE_HEIGHT * (schools.size() + (showSpellStyleTooltip ? 1 : 0));
            }
            return schools.isEmpty() ? 0 : LINE_HEIGHT;
        }

        @Override
        public int getWidth(@NotNull Font font) {
            if (!Screen.hasShiftDown()) {
                return Math.max(showSpellStyleTooltip ? font.width(name) + 14 : 0, 11 * schools.size());
            }
            int width = showSpellStyleTooltip ? TEXT_INDENT + font.width(Component.translatable("ars_nouveau.tooltip.can_be_styled")) : 0;
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
            if (showSpellStyleTooltip) {
                font.drawInBatch(Component.translatable("ars_nouveau.tooltip.can_be_styled"), x + TEXT_INDENT, y, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                y += LINE_HEIGHT;
            }
            for (SpellSchool school : schools) {
                font.drawInBatch(school.getTextComponent(), x + TEXT_INDENT, y, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                y += LINE_HEIGHT;
            }
        }

        @Override
        public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics guiGraphics) {
            boolean shift = Screen.hasShiftDown();
            if (showSpellStyleTooltip) {
                DocAssets.BlitInfo icon = DocAssets.SPELL_STYLE_TOOLTIP_ICON;
                int iconX = shift ? x - 1 : x + font.width(name) + 3;
                int iconY = shift ? y : y - 12;
                guiGraphics.blit(icon.location(), iconX, iconY, 11, 8, icon.u(), icon.v(), icon.width(), icon.height(), icon.width(), icon.height());
                if (shift) {
                    y += LINE_HEIGHT;
                }
            }
            for (SpellSchool school : schools) {
                guiGraphics.blit(school.getTexturePath(), x - 2, y - 2, 0, 0, 12, 12, 12, 12);
                if (shift) {
                    y += LINE_HEIGHT;
                } else {
                    x += 11;
                }
            }
        }
    }
}
