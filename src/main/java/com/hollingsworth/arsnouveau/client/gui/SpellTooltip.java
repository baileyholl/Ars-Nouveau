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
            return showName ? 28 : 20;
        }

        @Override
        public int getWidth(Font pFont) {
            return 4 + spellCaster.getSpell().size() * 16;
        }

        @Override
        public void renderText(Font pFont, int pX, int pY, Matrix4f pMatrix, MultiBufferSource.BufferSource pBufferSource) {

            if (showName) {
                pFont.drawInBatch(Component.literal(spellCaster.getSpellName()), (float) (pX + 4), (float) pY, -1, true, pMatrix, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
            }

        }

        @Override
        public void renderImage(Font pFont, int pX, int pY, GuiGraphics pGuiGraphics) {
            var spell = spellCaster.getSpell();
            for (int i = 0, recipeSize = spell.size(); i < recipeSize; i++) {
                AbstractSpellPart part = spell.get(i);
                RenderUtils.drawSpellPart(part, pGuiGraphics, pX + i * 16, pY + (showName ? 10 : 0), 16, false);
            }
        }

    }
}

