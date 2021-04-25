package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.RitualButton;
import com.hollingsworth.arsnouveau.common.items.RitualBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSetRitual;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

public class GuiRitualBook extends BaseBook{
    public String ritualDesc = "";
    public Minecraft mc;
    @Override
    public void init() {
        super.init();
        addButton(new RitualButton(this, bookLeft + 15, bookTop +15, b->ritualDesc = ((RitualButton)b).desc, "Digging the Well", "Digs a vertical shaft down to bedrock, filling in liquid blocks on the sides."));
        addButton(new GuiImageButton(bookRight - 100, bookBottom - 40, 0,0,46, 18, 46, 18, "textures/gui/create_button.png", (n)->{
            Networking.INSTANCE.sendToServer(new PacketSetRitual("dig", mc.player.getMainHandItem().getItem() instanceof RitualBook));
        }));
        this.mc = this.minecraft;
    }

    public static void open(){
        Minecraft.getInstance().setScreen(new GuiRitualBook());
    }

    @Override
    public void drawBackgroundElements(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(stack, mouseX, mouseY, partialTicks);
        ITextProperties itextproperties = new StringTextComponent(ritualDesc);
        minecraft.font.drawWordWrap(itextproperties, bookLeft +155, bookTop +20, 120, 0);
        minecraft.font.draw(stack,"Select", 197, 159,  0);

        //10053171
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);

    }
}
