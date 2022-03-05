package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.SoundButton;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SliderButton;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SoundScreen extends BaseBook{

    public int casterSlot;
    public SoundScreen(ConfiguredSpellSound configuredSpellSound, int slot) {
        super();
        volume = configuredSpellSound.volume;
        pitch = configuredSpellSound.pitch;
        selectedSound = configuredSpellSound.sound;
        casterSlot = slot;
    }

    public SliderButton volumeSlider;
    public SliderButton pitchSlider;


    public double volume;
    public double pitch;
    public SpellSound selectedSound;
    public SoundButton selectedButton;
    @Override
    public void init() {
        super.init();
        volumeSlider = (SliderButton)buildSlider(new TranslatableComponent("ars_nouveau.sounds.volume").getString(), s -> volume, (settings, d) -> volume = d, 0, 100, 1)
                .createButton(Minecraft.getInstance().options, bookLeft + 28, bookTop + 49, 100);
        pitchSlider = (SliderButton)buildSlider(new TranslatableComponent("ars_nouveau.sounds.pitch").getString(), s -> pitch, (settings, d) -> pitch = d, 0, 200, 1)
                .createButton(Minecraft.getInstance().options, bookLeft + 28, bookTop + 89, 100);

        addRenderableWidget(volumeSlider);
        addRenderableWidget(pitchSlider);
        addRenderableWidget(new GuiImageButton(bookLeft + 55, bookBottom - 36, 0,0,37, 12, 37, 12, "textures/gui/save_icon.png", this::onSaveClick));

        selectedButton = new SoundButton(this, bookLeft + 70, bookTop + 132, ArsNouveauAPI.getInstance().getSpellSoundsRegistry().values().stream().toList().get(0), (b) -> {
            ((SoundButton)b).sound = null;
            selectedSound = null;
        });
        addRenderableWidget(selectedButton);
        addPresets();
    }

    public void addPresets(){
        final int PER_ROW = 6;
        final int MAX_ROWS = 6;
        boolean nextPage = false;
        int xStart = bookLeft + 154;
        int adjustedRowsPlaced = 0;
        int yStart = bookTop + 22;
        int adjustedXPlaced = 0;
        List<SpellSound> sounds = ArsNouveauAPI.getInstance().getSpellSoundsRegistry().values().stream().toList();
        for(int i = 0; i < sounds.size(); i++){
            SpellSound part = sounds.get(i);

            if(adjustedXPlaced >= PER_ROW){
                adjustedRowsPlaced++;
                adjustedXPlaced = 0;
            }

            if(adjustedRowsPlaced > MAX_ROWS){
                if(nextPage){
                    break;
                }
                nextPage = true;
                adjustedRowsPlaced = 0;
            }
            int xOffset = 20 * ((adjustedXPlaced ) % PER_ROW) + (nextPage ? 134 :0);
            int yPlace = adjustedRowsPlaced * 18 + yStart;

            SoundButton cell = new SoundButton(this, xStart + xOffset, yPlace, part, this::onSoundClick);
            addRenderableWidget(cell);
            adjustedXPlaced++;
        }
    }

    public void setFromPreset(int r, int g, int b){
    }

    public void onSoundClick(Button button){
        if(button instanceof SoundButton){
            SoundButton soundButton = (SoundButton)button;
            selectedSound = soundButton.sound;
            selectedButton.sound = selectedSound;
        }
    }

    public void onSaveClick(Button button){
        //Networking.INSTANCE.sendToServer(new PacketUpdateSpellColors(slot, red, green, blue));
    }

    @Override
    public void drawBackgroundElements(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(stack, mouseX, mouseY, partialTicks);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/slider_gilding.png"), 22, 47, 0, 0, 112, 104,112,104, stack);
        int color = -8355712;
        minecraft.font.draw(stack, new TranslatableComponent("ars_nouveau.sounds.title").getString(), 51, 24,  color);
        minecraft.font.draw(stack, new TranslatableComponent("ars_nouveau.color_gui.save").getString(), 67, 160,  color);

    }

    @Override
    public void drawForegroundElements(int mouseX, int mouseY, float partialTicks) {
        super.drawForegroundElements(mouseX, mouseY, partialTicks);
    }

    protected ProgressOption buildSlider(String key, Function<Options, Double> getter, BiConsumer<Options, Double> setter, double min, double max, float step){
        return new ProgressOption(key, min, max, step, getter, setter, (settings, optionValues) -> new TextComponent(key + (int)optionValues.get(settings)));
    }
}
