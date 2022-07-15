package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.hollingsworth.arsnouveau.client.gui.BookSlider;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.SoundButton;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSetSound;
import com.hollingsworth.arsnouveau.setup.SoundRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

import java.util.List;

public class SoundScreen extends BaseBook {

    public int casterSlot;

    public SoundScreen(ConfiguredSpellSound configuredSpellSound, int slot) {
        super();
        volume = configuredSpellSound.volume * 100;
        pitch = configuredSpellSound.pitch * 100;
        selectedSound = configuredSpellSound.sound;
        casterSlot = slot;
    }

    public BookSlider volumeSlider;
    public BookSlider pitchSlider;


    public double volume;
    public double pitch;
    public SpellSound selectedSound;
    public SoundButton selectedButton;

    @Override
    public void init() {
        super.init();

        volumeSlider = buildSlider(bookLeft + 28, bookTop + 49, Component.translatable("ars_nouveau.sounds.volume"), Component.empty(), volume);
        pitchSlider = buildSlider(bookLeft + 28, bookTop + 89, Component.translatable("ars_nouveau.sounds.pitch"), Component.empty(), pitch);

        addRenderableWidget(volumeSlider);
        addRenderableWidget(pitchSlider);
        addRenderableWidget(new GuiImageButton(bookLeft + 25, bookBottom - 36, 0, 0, 37, 12, 37, 12, "textures/gui/save_icon.png", this::onSaveClick));
        GuiImageButton testButton = new GuiImageButton(bookLeft + 90, bookBottom - 36, 0, 0, 37, 12, 37, 12, "textures/gui/sound_test_icon.png", this::onTestClick);
        testButton.soundDisabled = true;
        addRenderableWidget(testButton);

        selectedButton = new SoundButton(this, bookLeft + 69, bookTop + 131, selectedSound, (b) -> {
            ((SoundButton) b).sound = SoundRegistry.EMPTY_SPELL_SOUND;
            selectedSound = SoundRegistry.EMPTY_SPELL_SOUND;
        });
        addRenderableWidget(selectedButton);
        addPresets();
    }

    public void addPresets() {
        final int PER_ROW = 6;
        final int MAX_ROWS = 6;
        boolean nextPage = false;
        int xStart = bookLeft + 154;
        int adjustedRowsPlaced = 0;
        int yStart = bookTop + 22;
        int adjustedXPlaced = 0;
        List<SpellSound> sounds = ArsNouveauAPI.getInstance().getSpellSoundsRegistry().values().stream().toList();
        for (int i = 0; i < sounds.size(); i++) {
            SpellSound part = sounds.get(i);

            if (adjustedXPlaced >= PER_ROW) {
                adjustedRowsPlaced++;
                adjustedXPlaced = 0;
            }

            if (adjustedRowsPlaced > MAX_ROWS) {
                if (nextPage) {
                    break;
                }
                nextPage = true;
                adjustedRowsPlaced = 0;
            }
            int xOffset = 20 * ((adjustedXPlaced) % PER_ROW) + (nextPage ? 134 : 0);
            int yPlace = adjustedRowsPlaced * 18 + yStart;

            SoundButton cell = new SoundButton(this, xStart + xOffset, yPlace, part, this::onSoundClick);
            addRenderableWidget(cell);
            adjustedXPlaced++;
        }
    }

    public void onSoundClick(Button button) {
        if (button instanceof SoundButton soundButton) {
            selectedSound = soundButton.sound;
            selectedButton.sound = selectedSound;
        }
    }

    public void onTestClick(Button button) {
        if (selectedSound == null)
            return;
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        BlockPos pos = localPlayer.getOnPos().above(2);
        localPlayer.level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), selectedSound.getSoundEvent(), SoundSource.PLAYERS, (float) volume / 100f, (float) pitch / 100f, false);
    }

    public void onSaveClick(Button button) {
        Networking.INSTANCE.sendToServer(new PacketSetSound(casterSlot, selectedSound == null ? ConfiguredSpellSound.EMPTY : new ConfiguredSpellSound(selectedSound, (float) volume / 100f, (float) pitch / 100f)));
    }

    @Override
    public void drawBackgroundElements(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(stack, mouseX, mouseY, partialTicks);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/sound_slider_gilding.png"), 22, 47, 0, 0, 112, 104, 112, 104, stack);
        int color = -8355712;
        minecraft.font.draw(stack, Component.translatable("ars_nouveau.sounds.title").getString(), 51, 24, color);
        minecraft.font.draw(stack, Component.translatable("ars_nouveau.color_gui.save").getString(), 37, 160, color);
        minecraft.font.draw(stack, Component.translatable("ars_nouveau.sounds.test").getString(), 102, 160, color);

    }

    @Override
    public void drawForegroundElements(int mouseX, int mouseY, float partialTicks) {
        super.drawForegroundElements(mouseX, mouseY, partialTicks);
    }
}
