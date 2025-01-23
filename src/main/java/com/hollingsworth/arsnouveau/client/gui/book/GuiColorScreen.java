package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.ParticleTimeline;
import com.hollingsworth.arsnouveau.api.particle.configurations.BurstConfiguration;
import com.hollingsworth.arsnouveau.api.particle.configurations.SpiralConfiguration;
import com.hollingsworth.arsnouveau.client.gui.BookSlider;
import com.hollingsworth.arsnouveau.client.gui.buttons.ANButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.RainbowParticleColor;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateParticleTimeline;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateSpellColorAll;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateSpellColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;

import java.util.ArrayList;
import java.util.List;

public class GuiColorScreen extends BaseBook {
    // Cache color values from constructor because we can only create widgets during init
    double startRed;
    double startGreen;
    double startBlue;

    public int slot;
    public int page = 0;

    public BookSlider redW;
    public BookSlider greenW;
    public BookSlider blueW;
    public InteractionHand stackHand;
    public List<ANButton> buttons = new ArrayList<>();

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
        addRenderableWidget(new GuiImageButton(bookLeft + 25, bookBottom - 30, 0, 0, 37, 12, 37, 12, "textures/gui/save_icon.png", this::onSaveClick));
        addRenderableWidget(new GuiImageButton(bookLeft + 75, bookBottom - 30, 0, 0, 37, 12, 37, 12, "textures/gui/save_icon.png", this::onSaveAllClick));

        layoutPageOne();

        var next = addRenderableWidget(new PageButton(bookRight - 20, bookBottom - 10, true, this::onPageIncrease, true));
        next.visible = true;
        next.active = true;
    }

    private void layoutPageOne() {
        addPresetColorButton(131, 44, ParticleColor.DEFAULT, "textures/gui/color_icons/purple_color_icon.png");
        addPresetColorButton(131, 68, ParticleColor.YELLOW, "textures/gui/color_icons/yellow_color_icon.png");
        addPresetColorButton(131, 92, ParticleColor.BLUE, "textures/gui/color_icons/blue_color_icon.png");
        addPresetColorButton(131, 116, ParticleColor.RED, "textures/gui/color_icons/red_color_icon.png");
        addPresetColorButton(131, 140, ParticleColor.GREEN, "textures/gui/color_icons/green_color_icon.png");

        // column 2
        addColorButton(73, 44,
                "textures/gui/color_icons/rainbow_color_icon.png", () -> Networking.sendToServer(new PacketUpdateSpellColors(slot, new RainbowParticleColor(0, 0, 0), this.stackHand == InteractionHand.MAIN_HAND)));
        addPresetColorButton(73, 68, ParticleColor.ORANGE, "textures/gui/color_icons/orange_color_icon.png");
        addPresetColorButton(73, 92, ParticleColor.CYAN, "textures/gui/color_icons/cyan_color_icon.png");
        addPresetColorButton(73, 116, ParticleColor.PINK, "textures/gui/color_icons/pink_color_icon.png");
        addPresetColorButton(73, 140, ParticleColor.LIME, "textures/gui/color_icons/lime_color_icon.png");

    }

    private void layoutPageTwo() {

        addPresetColorButton(131, 44, ParticleColor.WHITE, "textures/gui/color_icons/white_color_icon.png");
        addPresetColorButton(131, 68, ParticleColor.MAGENTA, "textures/gui/color_icons/magenta_color_icon.png");
        addPresetColorButton(131, 92, ParticleColor.LIGHT_BLUE, "textures/gui/color_icons/light_blue_color_icon.png");

        addPresetColorButton(73, 44, ParticleColor.BLACK, "textures/gui/color_icons/black_color_icon.png");
        addPresetColorButton(73, 68, ParticleColor.BROWN, "textures/gui/color_icons/brown_color_icon.png");
        addPresetColorButton(73, 92, ParticleColor.GRAY, "textures/gui/color_icons/gray_color_icon.png");

    }

    private void layoutPageThree() {
        //original page
        addColorButton(131, 44, "textures/gui/color_icons/purple_color_icon.png", () -> setFromPreset(255, 25, 180));
        addPresetColorButton(131, 68, ParticleColor.BROWN, "textures/gui/color_icons/default_color_icon.png");
        addColorButton(131, 92, "textures/gui/color_icons/blue_color_icon.png", () -> setFromPreset(30, 25, 255));
        addColorButton(131, 116, "textures/gui/color_icons/red_color_icon.png", () -> setFromPreset(255, 25, 25));
        addColorButton(131, 140, "textures/gui/color_icons/green_color_icon.png", () -> setFromPreset(25, 255, 25));
        addColorButton(73, 44, "textures/gui/color_icons/yellow_color_icon.png", () -> setFromPreset(255, 255, 25));
        addPresetColorButton(73, 68, ParticleColor.WHITE, "textures/gui/color_icons/white_color_icon.png");
        addColorButton(73, 92, "textures/gui/color_icons/orange_color_icon.png", () -> setFromPreset(255, 90, 1));
        addPresetColorButton(73, 116, ParticleColor.CYAN, "textures/gui/color_icons/cyan_color_icon.png");
        addRenderableWidget(new GuiImageButton(bookRight - 73, bookTop + 140, 0, 0, 48, 11, 48, 11,
                "textures/gui/color_icons/white_color_icon.png", (_2) -> Networking.sendToServer(new PacketUpdateSpellColors(slot, new RainbowParticleColor(0, 0, 0), this.stackHand == InteractionHand.MAIN_HAND))));
    }


    protected void addColorButton(int x, int y, String texturePath, Runnable onClick) {
        GuiImageButton pWidget = new GuiImageButton(bookRight - x, bookTop + y, 0, 0, 48, 11, 48, 11,
                texturePath, (_2) -> onClick.run());
        addRenderableWidget(pWidget);
        buttons.add(pWidget);
    }

    protected void addPresetColorButton(int x, int y, ParticleColor color, String texturePath) {
        addColorButton(x, y, texturePath, () -> setFromPreset(color));
    }


    public void setFromPreset(int r, int g, int b) {
        redW.setValue(r);
        greenW.setValue(g);
        blueW.setValue(b);
    }

    public void setFromPreset(ParticleColor preset) {
        redW.setValue(Mth.clamp(preset.getRed() * 255.0, 1, 255));
        greenW.setValue(Mth.clamp(preset.getGreen() * 255.0, 1, 255));
        blueW.setValue(Mth.clamp(preset.getBlue() * 255.0, 1, 255));
    }


    public void onSaveClick(Button button) {
        ParticleColor particleColor = new ParticleColor(redW.getValue(), greenW.getValue(), blueW.getValue());
        Networking.sendToServer(new PacketUpdateSpellColors(slot, particleColor, this.stackHand == InteractionHand.MAIN_HAND));
        Networking.sendToServer(new PacketUpdateParticleTimeline(slot, new ParticleTimeline(new SpiralConfiguration(ModParticles.CUSTOM_TYPE.get()), new BurstConfiguration(GlowParticleData.createData(particleColor))), this.stackHand == InteractionHand.MAIN_HAND));
    }

    public void onSaveAllClick(Button button) {
        Networking.sendToServer(new PacketUpdateSpellColorAll(slot, new ParticleColor(redW.getValue(), greenW.getValue(), blueW.getValue()), this.stackHand == InteractionHand.MAIN_HAND));
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(ArsNouveau.prefix( "textures/gui/slider_gilding.png"), 22, 47, 0, 0, 112, 104, 112, 104);
        int color = -8355712;
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.title").getString(), 51, 24, color, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.presets").getString(), 159, 24, color, false);

        if (page == 0) {
            graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.default").getString(), 170, 46, color, false);
            graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.yellow").getString(), 170, 70, color, false);
            graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.blue").getString(), 170, 94, color, false);
            graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.red").getString(), 170, 118, color, false);
            graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.green").getString(), 170, 142, color, false);
            graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.rainbow").getString(), 228, 46, color, false);
            graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.orange").getString(), 228, 70, color, false);
            graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.cyan").getString(), 228, 94, color, false);
            graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.pink").getString(), 228, 118, color, false);
            graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.lime").getString(), 228, 142, color, false);
        } else {
            graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.white").getString(), 170, 46, color, false);
            graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.magenta").getString(), 170, 70, color, false);
            graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.light_blue").getString(), 170, 94, color, false);

            graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.black").getString(), 228, 46, color, false);
            graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.brown").getString(), 228, 70, color, false);
            graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.gray").getString(), 228, 94, color, false);

        }
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.save").getString(), 37, 160, color, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.save_all").getString(), 87, 160, color, false);

    }

    public int getNumPages() {
        return 2;
    }

    public void onPageIncrease(Button b) {
        page = (page + 1) % getNumPages();
        clearButtons(buttons);
        switch (page) {
            default -> layoutPageOne();
            case 1 -> layoutPageTwo();
            case 2 -> layoutPageThree();
        }
    }


}
