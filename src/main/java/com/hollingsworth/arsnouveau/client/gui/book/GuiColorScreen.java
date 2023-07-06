package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.BookSlider;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.RainbowParticleColor;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateSpellColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

public class GuiColorScreen extends BaseBook {
    // Cache color values from constructor because we can only create widgets during init
    double startRed;
    double startGreen;
    double startBlue;

    public int slot;

    public BookSlider redW;
    public BookSlider greenW;
    public BookSlider blueW;
    public InteractionHand stackHand;

    protected GuiColorScreen(double startRed, double startGreen, double startBlue, int forSpellSlot, InteractionHand stackHand) {
        super();
        this.startRed = startRed;
        this.startGreen = startGreen;
        this.startBlue = startBlue;
        this.slot = forSpellSlot;
        this.stackHand = stackHand;
    }

    @Override
    public void init() {
        super.init();
        redW = buildSlider(bookLeft + 28, bookTop + 49, Component.translatable("ars_nouveau.color_gui.red_slider"), Component.empty(), startRed);
        greenW = buildSlider(bookLeft + 28, bookTop + 89, Component.translatable("ars_nouveau.color_gui.green_slider"), Component.empty(), startGreen);
        blueW = buildSlider(bookLeft + 28, bookTop + 129, Component.translatable("ars_nouveau.color_gui.blue_slider"), Component.empty(), startBlue);
        addRenderableWidget(redW);
        addRenderableWidget(greenW);
        addRenderableWidget(blueW);
        addRenderableWidget(new GuiImageButton(bookLeft + 55, bookBottom - 36, 0, 0, 37, 12, 37, 12, "textures/gui/save_icon.png", this::onSaveClick));


        // Default
        addRenderableWidget(new GuiImageButton(bookRight - 131, bookTop + 44, 0, 0, 48, 11, 48, 11,
                "textures/gui/default_color_icon.png", (_2) -> setFromPreset(255, 25, 180)));
        // Purple
        addRenderableWidget(new GuiImageButton(bookRight - 131, bookTop + 68, 0, 0, 48, 11, 48, 11,
                "textures/gui/purple_color_icon.png", (_2) -> setFromPreset(80, 25, 255)));

        // Blue
        addRenderableWidget(new GuiImageButton(bookRight - 131, bookTop + 92, 0, 0, 48, 11, 48, 11,
                "textures/gui/blue_color_icon.png", (_2) -> setFromPreset(30, 25, 255)));

        // Red
        addRenderableWidget(new GuiImageButton(bookRight - 131, bookTop + 116, 0, 0, 48, 11, 48, 11,
                "textures/gui/red_color_icon.png", (_2) -> setFromPreset(255, 25, 25)));

        // Green
        addRenderableWidget(new GuiImageButton(bookRight - 131, bookTop + 140, 0, 0, 48, 11, 48, 11,
                "textures/gui/green_color_icon.png", (_2) -> setFromPreset(25, 255, 25)));

        // Yellow
        addRenderableWidget(new GuiImageButton(bookRight - 73, bookTop + 44, 0, 0, 48, 11, 48, 11,
                "textures/gui/yellow_color_icon.png", (_2) -> setFromPreset(255, 255, 25)));
        // White
        addRenderableWidget(new GuiImageButton(bookRight - 73, bookTop + 68, 0, 0, 48, 11, 48, 11,
                "textures/gui/white_color_icon.png", (_2) -> setFromPreset(255, 255, 255)));

        // Orange
        addRenderableWidget(new GuiImageButton(bookRight - 73, bookTop + 92, 0, 0, 48, 11, 48, 11,
                "textures/gui/orange_color_icon.png", (_2) -> setFromPreset(255, 90, 1)));

        // Cyan
        addRenderableWidget(new GuiImageButton(bookRight - 73, bookTop + 116, 0, 0, 48, 11, 48, 11,
                "textures/gui/cyan_color_icon.png", (_2) -> setFromPreset(25, 255, 255)));

        // rainbow
        addRenderableWidget(new GuiImageButton(bookRight - 73, bookTop + 140, 0, 0, 48, 11, 48, 11,
                "textures/gui/white_color_icon.png", (_2) -> {
            Networking.INSTANCE.sendToServer(new PacketUpdateSpellColors(slot, new RainbowParticleColor(0,0,0), this.stackHand == InteractionHand.MAIN_HAND));
        }));
    }

    public void setFromPreset(int r, int g, int b) {
        redW.setValue(r);
        greenW.setValue(g);
        blueW.setValue(b);
    }


    public void onSaveClick(Button button) {
        Networking.INSTANCE.sendToServer(new PacketUpdateSpellColors(slot, new ParticleColor(redW.getValue(), greenW.getValue(), blueW.getValue()), this.stackHand == InteractionHand.MAIN_HAND));
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(ArsNouveau.MODID, "textures/gui/slider_gilding.png"), 22, 47, 0, 0, 112, 104, 112, 104);
        int color = -8355712;
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.title").getString(), 51, 24, color, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.presets").getString(), 159, 24, color, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.default").getString(), 170, 46, color, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.purple").getString(), 170, 70, color, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.blue").getString(), 170, 94, color, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.red").getString(), 170, 118, color, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.green").getString(), 170, 142, color, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.yellow").getString(), 228, 46, color, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.white").getString(), 228, 70, color, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.orange").getString(), 228, 94, color, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.cyan").getString(), 228, 118, color, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.rainbow").getString(), 228, 142, color, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.save").getString(), 67, 160, color, false);
    }
}
