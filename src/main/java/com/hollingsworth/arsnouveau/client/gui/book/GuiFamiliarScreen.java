package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.client.gui.buttons.FamiliarButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketDispelFamiliars;
import com.hollingsworth.arsnouveau.common.network.PacketSummonFamiliar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class GuiFamiliarScreen extends BaseBook {

    public List<AbstractFamiliarHolder> familiars;
    public Screen parent;

    public GuiFamiliarScreen(List<AbstractFamiliarHolder> familiars, Screen parent) {
        this.familiars = familiars;
        this.parent = parent;
    }

    @Override
    public void init() {
        super.init();
        int xStart = bookLeft + 20;
        int yStart = bookTop + 34;
        final int PER_ROW = 6;
        int toLayout = Math.min(familiars.size(), PER_ROW * 5);
        for (int i = 0; i < toLayout; i++) {
            AbstractFamiliarHolder part = familiars.get(i);
            int xOffset = 20 * (i % PER_ROW);
            int yOffset = (i / PER_ROW) * 18;
            FamiliarButton cell = new FamiliarButton(xStart + xOffset, yStart + yOffset, part, this::onFamiliarClicked);
            addRenderableWidget(cell);
        }
        addRenderableWidget(new GuiImageButton(bookRight - 71, bookBottom - 13, 0, 0, 41, 12, 41, 12, "textures/gui/clear_icon.png", (e) -> {
            Minecraft.getInstance().setScreen(parent);
        }));

        addRenderableWidget(new GuiImageButton(bookRight - 131, bookBottom - 13, 0, 0, 41, 12, 41, 12, "textures/gui/clear_icon.png", (e) -> {
            Networking.sendToServer(new PacketDispelFamiliars());
        }));
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(ArsNouveau.prefix( "textures/gui/create_paper.png"), 216, 179, 0, 0, 56, 15, 56, 15);
        graphics.blit(ArsNouveau.prefix( "textures/gui/create_paper.png"), 156, 179, 0, 0, 56, 15, 56, 15);
        graphics.drawString(font, Component.translatable("ars_nouveau.spell_book_gui.familiar").getString(), 20, 24, -8355712, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.spell_book_gui.close"), 232, 183, -8355712, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.spell_book_gui.dispel"), 172, 183, -8355712, false);
    }

    public void onFamiliarClicked(Button button) {
        FamiliarButton button1 = (FamiliarButton) button;
        Networking.sendToServer(new PacketSummonFamiliar(button1.familiarHolder.getRegistryName()));
        Minecraft.getInstance().setScreen(null);
    }
}
