package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.gui.book.GuiRitualBook;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.StringTextComponent;

public class RitualButton extends GuiImageButton{
    GuiRitualBook parent;
    public String desc;
    public String name;
    public AbstractRitual ritual;

    public RitualButton(GuiRitualBook parent, int x, int y, IPressable onPress, AbstractRitual ritual) {
        super(x, y,0,0,100, 16, 100,16, "textures/gui/create_button.png", onPress);
        this.parent = parent;
        this.ritual = ritual;
        this.desc = ritual.getDescription();
        this.name = ritual.getName();
    }

    @Override
    public void render(MatrixStack ms, int parX, int parY, float partialTicks) {
      //  super.render(ms, parX, parY, partialTicks);
        parent.mc.font.drawWordWrap(new StringTextComponent(name), x, y +5,  123, 123);
    }
}
