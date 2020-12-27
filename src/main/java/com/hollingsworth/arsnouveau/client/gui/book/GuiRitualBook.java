package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.RitualButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

public class GuiRitualBook extends BaseBook{
    public String ritualDesc = "";

    @Override
    public void init() {
        super.init();
        addButton(new RitualButton(this, bookLeft + 15, bookTop +20, (b)->ritualDesc = ((RitualButton)b).desc, "This is a ritual that does super cool stuff"));
        addButton(new GuiImageButton(bookRight - 95, bookBottom - 28, 0,0,46, 18, 46, 18, "textures/gui/create_button.png", (n)->{}));
    }

    public static void open(){
        Minecraft.getInstance().displayGuiScreen(new GuiRitualBook());
    }

    @Override
    public void drawBackgroundElements(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(stack, mouseX, mouseY, partialTicks);
        ITextProperties itextproperties = new StringTextComponent(ritualDesc);
        minecraft.fontRenderer.func_238418_a_(itextproperties, bookLeft +145, bookTop +15, 120, 100);
        minecraft.fontRenderer.drawString(stack,"Select", 185, 157,  0);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);

    }
}
