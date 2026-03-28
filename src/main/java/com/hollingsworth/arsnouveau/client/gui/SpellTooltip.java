package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.NotNull;

public record SpellTooltip(Spell spell, boolean showName) implements TooltipComponent {


    public SpellTooltip(AbstractCaster<?> spellcaster, boolean showName) {
        this(spellcaster.getSpell(), showName);
    }

    public SpellTooltip(AbstractCaster<?> spellcaster) {
        this(spellcaster, false);
    }

    public static class SpellTooltipRenderer implements ClientTooltipComponent {
        private final boolean showName;
        public final Spell spell;

        public SpellTooltipRenderer(SpellTooltip pSpellTooltip) {
            this.showName = pSpellTooltip.showName();
            spell = pSpellTooltip.spell;
        }

        @Override
        public int getHeight(@NotNull Font pFont) {
            return (showName ? 28 : 20) + (spell.size() / 10) * 16;
        }

        @Override
        public int getWidth(@NotNull Font pFont) {
            return 4 + Math.min(spell.size(), 10) * 16;
        }

        @Override
        public void renderText(@NotNull GuiGraphics pGuiGraphics, @NotNull Font pFont, int pX, int pY) {
            if (showName) {
                pGuiGraphics.drawString(pFont, Component.literal(spell.name()), pX + 4, pY, -1);
            }
        }

        @Override
        public void renderImage(@NotNull Font pFont, int pX, int pY, int pWidth, int pHeight, @NotNull GuiGraphics pGuiGraphics) {
            for (int i = 0; i < spell.size(); i++) {
                int yOffset = i / 10;
                AbstractSpellPart part = spell.get(i);
                RenderUtils.drawSpellPart(part, pGuiGraphics, pX + (i % 10) * 16, pY + (showName ? 10 : 0) + yOffset * 16, 16, false);
            }
        }

    }
}

