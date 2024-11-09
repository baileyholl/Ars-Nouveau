package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SchoolTooltip(List<SpellSchool> schools) implements TooltipComponent {

    public static class SchoolTooltipRenderer implements ClientTooltipComponent {

        private final List<SpellSchool> schools;

        public SchoolTooltipRenderer(SchoolTooltip pSchoolTooltip) {
            this.schools = pSchoolTooltip.schools();
        }

        @Override
        public int getHeight() {
            return 16;
        }

        @Override
        public int getWidth(@NotNull Font font) {
            return 16 * schools.size();
        }

        @Override
        public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics guiGraphics) {
            for (SpellSchool school : schools) {
                guiGraphics.blit(school.getTexturePath(), x, y, 0, 0, 16, 16, 16, 16);
                x += 16;
            }
        }
    }
}
