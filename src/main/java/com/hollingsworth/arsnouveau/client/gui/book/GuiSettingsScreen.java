package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class GuiSettingsScreen extends BaseBook{

    public Screen parent;

    public GuiSettingsScreen(@Nullable Screen parent) {
        super();
        this.parent = parent;
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new GuiImageButton(bookRight - 71, bookBottom - 13, 0,0,41, 12, 41, 12, "textures/gui/clear_icon.png", (e) -> {Minecraft.getInstance().setScreen(parent);}));

    }

    public void onCloseClick(Button button) {
        if(parent != null)
            Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public void drawBackgroundElements(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(stack, mouseX, mouseY, partialTicks);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/create_paper.png"), 216, 179, 0, 0, 56, 15,56,15, stack);
        minecraft.font.draw(stack, new TranslatableComponent("ars_nouveau.spell_book_gui.close"), 238, 183, -8355712);
    }
}
