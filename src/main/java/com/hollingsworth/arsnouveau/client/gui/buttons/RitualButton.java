package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.client.gui.book.GuiRitualBook;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.StringTextComponent;

public class RitualButton extends GuiImageButton{
    GuiRitualBook parent;
    public String desc;
    public String name;
    public RitualButton(GuiRitualBook parent, int x, int y, IPressable onPress, String name, String desc) {
        super(x, y,0,0,100, 16, 100,16, "textures/gui/create_button.png", onPress);
        this.parent = parent;
        this.desc = desc;
        this.name = name;
    }

    @Override
    public void render(MatrixStack ms, int parX, int parY, float partialTicks) {
      //  super.render(ms, parX, parY, partialTicks);
        parent.mc.fontRenderer.func_238418_a_(new StringTextComponent(name), x, y +5,  123, 123);
    }
}
