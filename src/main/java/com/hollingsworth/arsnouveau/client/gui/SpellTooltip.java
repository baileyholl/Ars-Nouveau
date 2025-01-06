package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public record SpellTooltip(AbstractCaster<?> spellcaster, boolean showName) implements TooltipComponent {

    public SpellTooltip(AbstractCaster<?> spellcaster) {
        this(spellcaster, false);
    }

    public static class SpellTooltipRenderer implements ClientTooltipComponent {
        private final AbstractCaster<?> spellCaster;
        private final boolean showName;

        public SpellTooltipRenderer(SpellTooltip pSpellTooltip) {
            this.spellCaster = pSpellTooltip.spellcaster();
            this.showName = pSpellTooltip.showName();
        }

        @Override
        public int getHeight() {

            return (showName ? 28 : 20) +  (spellCaster.getSpell().size() / 10) * 16;
        }

        @Override
        public int getWidth(@NotNull Font pFont) {
            return 4 + Math.min(spellCaster.getSpell().size(), 10) * 16;
        }

        @Override
        public void renderText(@NotNull Font pFont, int pX, int pY, @NotNull Matrix4f pMatrix, MultiBufferSource.@NotNull BufferSource pBufferSource) {

            if (showName) {
                pFont.drawInBatch(Component.literal(spellCaster.getSpellName()), (float) (pX + 4), (float) pY, -1, true, pMatrix, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
            }

        }

        @Override
        public void renderImage(@NotNull Font pFont, int pX, int pY, @NotNull GuiGraphics pGuiGraphics) {
            var spell = spellCaster.getSpell();
            for (int i = 0; i <  spell.size(); i++) {
                int yOffset = i / 10;
                AbstractSpellPart part = spell.get(i);
                RenderUtils.drawSpellPart(part, pGuiGraphics, pX + (i % 10) * 16, pY + (showName ? 10 : 0) + yOffset * 16, 16, false);
            }
        }

    }
}

