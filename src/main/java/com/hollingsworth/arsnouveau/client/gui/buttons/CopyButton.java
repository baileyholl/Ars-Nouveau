package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CopyButton extends GuiImageButton {

    public CopyButton(GuiSpellBook guiSpellBook) {
        super(guiSpellBook.bookLeft + 126, guiSpellBook.bookBottom - 13, 0, 0, 16, 15, 16, 15, "textures/gui/copy_icon.png", guiSpellBook::onCopyOrExport);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        super.getTooltip(tooltip);
        tooltip.add(Component.translatable("ars_nouveau.spell_book_gui.copy_shift"));
    }

}
