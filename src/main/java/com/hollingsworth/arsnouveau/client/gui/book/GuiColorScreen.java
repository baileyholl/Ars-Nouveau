package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateSpellColors;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.OptionSlider;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class GuiColorScreen extends BaseBook {
    private final int FULL_WIDTH = 290;
    private final int FULL_HEIGHT = 194;

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
        redW = (OptionSlider)buildSlider("Red: ", s -> red, (settings, d) -> red = d).createWidget(Minecraft.getInstance().gameSettings, bookLeft + 28, bookTop + 49, 100);
        greenW = (OptionSlider)buildSlider("Green: ", s -> green, (settings, d) -> green = d).createWidget(Minecraft.getInstance().gameSettings, bookLeft + 28, bookTop + 89, 100);
        blueW = (OptionSlider)buildSlider("Blue: ", s -> blue, (settings, d) -> blue = d).createWidget(Minecraft.getInstance().gameSettings, bookLeft + 28, bookTop + 129, 100);
        addButton(redW);
        addButton(greenW);
        addButton(blueW);
        addButton(new GuiImageButton(bookLeft+ 55, bookBottom - 36, 0,0,37, 12, 37, 12, "textures/gui/save_icon.png", this::onSaveClick));
        addPresets();
    }

    public void addPresets(){
        // Default
        addButton(new GuiImageButton(bookRight - 131, bookTop + 44, 0,0,48, 11, 48, 11,
                "textures/gui/default_color_icon.png", (_2) -> {setFromPreset(255, 25, 180);}));
        // Purple
        addButton(new GuiImageButton(bookRight - 131, bookTop + 68, 0,0,48, 11, 48, 11,
                "textures/gui/purple_color_icon.png", (_2) -> {setFromPreset(80, 25, 255);}));

        // Blue
        addButton(new GuiImageButton(bookRight - 131, bookTop + 92, 0,0,48, 11, 48, 11,
                "textures/gui/blue_color_icon.png", (_2) -> {setFromPreset(30, 25, 255);}));

        // Red
        addButton(new GuiImageButton(bookRight - 131, bookTop + 116, 0,0,48, 11, 48, 11,
                "textures/gui/red_color_icon.png", (_2) -> {setFromPreset(255, 25, 25);}));

        // Green
        addButton(new GuiImageButton(bookRight - 131, bookTop + 140, 0,0,48, 11, 48, 11,
                "textures/gui/green_color_icon.png", (_2) -> {setFromPreset(25, 255, 25);}));

        // Yellow
        addButton(new GuiImageButton(bookRight - 73, bookTop + 44, 0,0,48, 11, 48, 11,
                "textures/gui/yellow_color_icon.png", (_2) -> {setFromPreset(255, 255, 25);}));
        // White
        addButton(new GuiImageButton(bookRight - 73, bookTop + 68, 0,0,48, 11, 48, 11,
                "textures/gui/white_color_icon.png", (_2) -> {setFromPreset(255, 255, 255);}));

        // Orange
        addButton(new GuiImageButton(bookRight - 73, bookTop + 92, 0,0,48, 11, 48, 11,
                "textures/gui/orange_color_icon.png", (_2) -> {setFromPreset(255, 90, 1);}));

        // Cyan
        addButton(new GuiImageButton(bookRight - 73, bookTop + 116, 0,0,48, 11, 48, 11,
                "textures/gui/cyan_color_icon.png", (_2) -> {setFromPreset(25, 255, 255);}));


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
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/slider_gilding.png"), 22, 47, 0, 0, 112, 104,112,104, stack);
        minecraft.fontRenderer.drawString(stack, "Spell Color", 51, 24,  12694931);
        minecraft.fontRenderer.drawString(stack, "Presets", 159, 24,  12694931);
        minecraft.fontRenderer.drawString(stack, "Default", 170, 46,  12694931);
        minecraft.fontRenderer.drawString(stack, "Purple", 170, 70,  12694931);
        minecraft.fontRenderer.drawString(stack, "Blue", 170, 94,  12694931);
        minecraft.fontRenderer.drawString(stack, "Red", 170, 118,  12694931);
        minecraft.fontRenderer.drawString(stack, "Green", 170, 142,  12694931);
        minecraft.fontRenderer.drawString(stack, "Yellow", 228, 46,  12694931);
        minecraft.fontRenderer.drawString(stack, "White", 228, 70,  12694931);
        minecraft.fontRenderer.drawString(stack, "Orange", 228, 94,  12694931);
        minecraft.fontRenderer.drawString(stack, "Cyan", 228, 118,  12694931);
       // minecraft.fontRenderer.drawString(stack, "Ice", 218, 115,  0);
        minecraft.fontRenderer.drawString(stack, "Save", 67, 160,  12694931);
    }

    protected SliderPercentageOption buildSlider(String key, Function<GameSettings, Double> getter, BiConsumer<GameSettings, Double> setter){
        return new SliderPercentageOption(key, 1.0D, 255.0D, 1.0F, getter, setter, (settings, optionValues) -> {
            return new StringTextComponent(key + (int)optionValues.get(settings));
        });
    }
}
