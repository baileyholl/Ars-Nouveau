package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.joml.Matrix4f;

import java.util.List;

public record SpellTooltip(ISpellCaster spellcaster, boolean showName) implements TooltipComponent {

    public SpellTooltip(ISpellCaster spellcaster) {
        this(spellcaster, false);
    }

    public static class SpellTooltipRenderer implements ClientTooltipComponent {
        private final ISpellCaster spellCaster;
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
            return 4 + spellCaster.getSpell().recipe.size() * 16;
        }

        @Override
        public void renderText(Font pFont, int pX, int pY, Matrix4f pMatrix, MultiBufferSource.BufferSource pBufferSource) {

            if (showName) {
                pFont.drawInBatch(Component.literal(spellCaster.getSpellName()), (float) (pX + 4), (float) pY, -1, true, pMatrix, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
            }

        }

        @Override
        public void renderImage(Font pFont, int pX, int pY, GuiGraphics pGuiGraphics) {
            List<AbstractSpellPart> recipe = spellCaster.getSpell().recipe;
            for (int i = 0, recipeSize = recipe.size(); i < recipeSize; i++) {
                AbstractSpellPart part = recipe.get(i);
                RenderUtils.drawSpellPart(part, pGuiGraphics, pX + i * 16, pY + (showName ? 10 : 0), 16, false);
            }
        }

    }
}

