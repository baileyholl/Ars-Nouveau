package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateSpellColors;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.OptionSlider;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.StringTextComponent;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class GuiColorScreen extends BaseBook {
    private final int FULL_WIDTH = 272;
    private final int FULL_HEIGHT = 180;

    double red;
    double blue;
    double green;
    int slot;

    protected GuiColorScreen(double red, double green, double blue, int slot) {
        super();
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.slot = slot;
    }

    OptionSlider redW;
    OptionSlider greenW;
    OptionSlider blueW;
    @Override
    public void init() {
        super.init();
        redW = (OptionSlider)buildSlider("Red: ", s -> red, (settings, d) -> red = d).createWidget(Minecraft.getInstance().gameSettings, bookLeft + 25, bookTop + 40, 100);
        greenW = (OptionSlider)buildSlider("Green: ", s -> green, (settings, d) -> green = d).createWidget(Minecraft.getInstance().gameSettings, bookLeft+ 25, bookTop +80, 100);
        blueW = (OptionSlider)buildSlider("Blue: ", s -> blue, (settings, d) -> blue = d).createWidget(Minecraft.getInstance().gameSettings, bookLeft+ 25, bookTop +120, 100);
        addButton(redW);
        addButton(greenW);
        addButton(blueW);
        addButton(new GuiImageButton(bookLeft+ 50, bookBottom - 35, 0,0,46, 18, 46, 18, "textures/gui/create_button.png", this::onSaveClick));
        addPresets();
    }

    public void addPresets(){
        // Default
        addButton(new GuiImageButton(bookRight - 125, bookTop + 35, 0,0,46, 18, 46, 18,
                "textures/gui/create_button.png", (_2) -> {setFromPreset(255, 25, 180);}));
        // Purple
        addButton(new GuiImageButton(bookRight - 125, bookTop + 60, 0,0,46, 18, 46, 18,
                "textures/gui/create_button.png", (_2) -> {setFromPreset(80, 25, 255);}));

        // blue
        addButton(new GuiImageButton(bookRight - 125, bookTop + 85, 0,0,46, 18, 46, 18,
                "textures/gui/create_button.png", (_2) -> {setFromPreset(30, 25, 255);}));

        // Red
        addButton(new GuiImageButton(bookRight - 125, bookTop + 110, 0,0,46, 18, 46, 18,
                "textures/gui/create_button.png", (_2) -> {setFromPreset(255, 25, 25);}));

        // Green
        addButton(new GuiImageButton(bookRight - 125, bookTop + 135, 0,0,46, 18, 46, 18,
                "textures/gui/create_button.png", (_2) -> {setFromPreset(25, 255, 25);}));

        // Yellow
        addButton(new GuiImageButton(bookRight - 65, bookTop + 35, 0,0,46, 18, 46, 18,
                "textures/gui/create_button.png", (_2) -> {setFromPreset(255, 255, 25);}));
        // White
        addButton(new GuiImageButton(bookRight - 65, bookTop + 60, 0,0,46, 18, 46, 18,
                "textures/gui/create_button.png", (_2) -> {setFromPreset(255, 255, 255);}));

        // Orange
        addButton(new GuiImageButton(bookRight - 65, bookTop + 85, 0,0,46, 18, 46, 18,
                "textures/gui/create_button.png", (_2) -> {setFromPreset(255, 90, 1);}));

        // Cyan
        addButton(new GuiImageButton(bookRight - 65, bookTop + 110, 0,0,46, 18, 46, 18,
                "textures/gui/create_button.png", (_2) -> {setFromPreset(25, 255, 255);}));


    }
    public void setFromPreset(int r, int g, int b){
        red = r;
        green = g;
        blue = b;
        redW.setSliderValue((r)/255.0);
        greenW.setSliderValue((g)/255.0);
        blueW.setSliderValue((b)/255.0);

    }

    public void onSaveClick(Button button){
//        List<String> ids = new ArrayList<>();
//        for(CraftingButton slot : craftingCells){
//            ids.add(slot.spellTag);
//        }
        Networking.INSTANCE.sendToServer(new PacketUpdateSpellColors(slot, red, green, blue));
    }

    @Override
    public void drawBackgroundElements(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(stack, mouseX, mouseY, partialTicks);
        minecraft.fontRenderer.drawString(stack, "Spell Color", 50, 20,  0);
        minecraft.fontRenderer.drawString(stack, "Presets", 150, 20,  0);
        minecraft.fontRenderer.drawString(stack, "Default", 153, 40,  0);
        minecraft.fontRenderer.drawString(stack, "Purple", 154, 65,  0);
        minecraft.fontRenderer.drawString(stack, "Blue", 160, 90,  0);
        minecraft.fontRenderer.drawString(stack, "Red", 160, 115,  0);
        minecraft.fontRenderer.drawString(stack, "Green", 155, 140,  0);
        minecraft.fontRenderer.drawString(stack, "Yellow", 215, 40,  0);
        minecraft.fontRenderer.drawString(stack, "White", 220, 65,  0);
        minecraft.fontRenderer.drawString(stack, "Orange", 212, 90,  0);
        minecraft.fontRenderer.drawString(stack, "Cyan", 218, 115,  0);
       // minecraft.fontRenderer.drawString(stack, "Ice", 218, 115,  0);
        minecraft.fontRenderer.drawString(stack, "Save", 60, 150,  0);
    }

    protected SliderPercentageOption buildSlider(String key, Function<GameSettings, Double> getter, BiConsumer<GameSettings, Double> setter){
        return new SliderPercentageOption(key, 1.0D, 255.0D, 1.0F, getter, setter, (settings, optionValues) -> {
            return new StringTextComponent(key + (int)optionValues.get(settings));
        });
    }
}
