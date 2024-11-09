package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SchoolTooltip(AbstractSpellPart part) implements TooltipComponent {

    public List<SpellSchool> schools() {
        return part.spellSchools;
    }

    public String name() {
        return Component.translatable("ars_nouveau.glyph_of", part.getLocaleName()).getString();
    }

    public static class SchoolTooltipRenderer implements ClientTooltipComponent {

        static final int offset = 64;
        private final List<SpellSchool> schools;
        private final String name;

        public SchoolTooltipRenderer(SchoolTooltip pSchoolTooltip) {
            this.schools = pSchoolTooltip.schools();
            this.name = pSchoolTooltip.name();
        }

        public SchoolTooltipRenderer(AbstractSpellPart part) {
            this.schools = part.spellSchools;
            this.name = part.getLocaleName();
        }

        @Override
        public int getHeight() {
            return 0;
        }

        @Override
        public int getWidth(@NotNull Font font) {
            return font.width(name) + offset + 16 * schools.size();
        }

        @Override
        public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics guiGraphics) {
            x += offset;
            for (SpellSchool school : schools) {
                guiGraphics.blit(school.getTexturePath(), x + font.width(name), y - 16, 0, 0, 16, 16, 16, 16);
                x += 16;
            }
        }
    }
}
