package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.client.gui.book.GuiRitualBook;

public class RitualButton extends GuiImageButton{
    GuiRitualBook parent;
    public String desc;
    public RitualButton(GuiRitualBook parent, int x, int y, IPressable onPress, String desc) {
        super(x, y,0,0,100, 16, 100,16, "textures/gui/create_button.png", onPress);
        this.parent = parent;
        this.desc = desc;
    }
}
