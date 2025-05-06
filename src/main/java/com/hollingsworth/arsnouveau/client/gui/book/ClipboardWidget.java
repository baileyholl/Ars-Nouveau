package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ClipboardWidget extends AbstractWidget {

    private final GuiSpellBook spellbook;
    public ResourceLocation image = ArsNouveau.prefix("textures/gui/clipboard.png");
    private Spell clipboard = new Spell();

    static int image_width = 80;
    static int image_height = 60;

    public ClipboardWidget(GuiSpellBook spellBook) {
        super(spellBook.bookRight + 10, spellBook.bookTop + 20, 0, 0, Component.empty());
        this.spellbook = spellBook;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        graphics.blit(image, x, spellbook.bookTop, image_width, 0, image_width, 20, image_width, image_height);
        // render the clipboard glyphs
        int clipboardSize = Math.max(2, Mth.ceil(clipboard.size() / 3F));
        // render the clipboard top, then clipboardSize middles, then the clipboard bottom backgrounds
        for (int i = 1; i <= clipboardSize; i++) {
            graphics.blit(image, x, spellbook.bookTop + 20 * i, image_width, 20, image_width, 20, image_width, image_height);
        }
        if (!clipboard.isEmpty()) {
            for (int i = 0; i < clipboard.size(); i++) {
                AbstractSpellPart part = clipboard.get(i);
                if (part != null) {
                    RenderUtils.drawSpellPart(part, graphics, x + 12 + 20 * (i % 3), spellbook.bookTop + 16 + 20 * (i / 3), 16, false, 0);
                }
            }
        }
        graphics.blit(image, x, spellbook.bookTop + (20 * clipboardSize + 12), image_width, image_height - 12, image_width, 12, image_width, image_height);
    }

    @Override
    public void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }


    public void setClipboard(Spell.Mutable clipboard) {
        this.clipboard = clipboard.immutable();
    }

}
