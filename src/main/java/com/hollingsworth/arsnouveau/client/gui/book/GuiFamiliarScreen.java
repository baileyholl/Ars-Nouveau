package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.client.gui.buttons.FamiliarButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSummonFamiliar;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class GuiFamiliarScreen extends BaseBook{
    public ArsNouveauAPI api;
    public List<AbstractFamiliarHolder> familiars;
    public GuiFamiliarScreen(ArsNouveauAPI api, List<AbstractFamiliarHolder> familiars){
        this.api = api;
        this.familiars = familiars;
    }

    @Override
    public void init() {
        super.init();
        layoutParts();
    }

    public void layoutParts(){
        int xStart = bookLeft + 20;
        int yStart = bookTop + 34;
        final int PER_ROW = 6;
        int toLayout = Math.min(familiars.size(), PER_ROW * 5);
        for (int i = 0; i < toLayout; i++) {
            AbstractFamiliarHolder part = familiars.get(i);
            int xOffset = 20 * (i % PER_ROW);
            int yOffset = (i / PER_ROW) * 18;
            FamiliarButton cell = new FamiliarButton(this, xStart + xOffset, yStart + yOffset, false, part);
            addWidget(cell);
        }
        addWidget(new GuiImageButton(bookRight - 71, bookBottom - 13, 0,0,41, 12, 41, 12, "textures/gui/clear_icon.png", (e) -> {Minecraft.getInstance().setScreen(null);}));

    }

    @Override
    public void drawBackgroundElements(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(stack, mouseX, mouseY, partialTicks);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/create_paper.png"), 216, 179, 0, 0, 56, 15,56,15, stack);
        minecraft.font.draw(stack,new TranslatableComponent("ars_nouveau.spell_book_gui.familiar").getString(), 20, 24, -8355712);
        minecraft.font.draw(stack, new TranslatableComponent("ars_nouveau.spell_book_gui.close"), 238, 183, -8355712);
    }

    public void onGlyphClick(Button button){
        FamiliarButton button1 = (FamiliarButton) button;
        Networking.INSTANCE.sendToServer(new PacketSummonFamiliar(button1.familiarHolder.id, Minecraft.getInstance().player.getId()));
        Minecraft.getInstance().setScreen(null);
    }
}
