package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import net.minecraft.network.chat.Component;

import java.util.List;

public class PasteButton extends GuiImageButton {
    private final GuiSpellBook guiSpellBook;

    public PasteButton(GuiSpellBook guiSpellBook) {
        super(guiSpellBook.bookLeft + 128 + 16, guiSpellBook.bookBottom - 13, 0, 0, 16, 15, 16, 15, "textures/gui/paste_icon.png", guiSpellBook::onPasteOrImport);
        this.guiSpellBook = guiSpellBook;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        super.getTooltip(tooltip);
        tooltip.add(Component.translatable("ars_nouveau.spell_book_gui.paste_shift"));
    }

//    @Override
//    public TooltipComponent getTooltipImage() {
//        if (guiSpellBook.clipboard == null || guiSpellBook.clipboard.isEmpty()) {
//            return null;
//        }
//        return new SpellTooltip(new Spell(guiSpellBook.clipboard.unsafeList()));
//    }

}
