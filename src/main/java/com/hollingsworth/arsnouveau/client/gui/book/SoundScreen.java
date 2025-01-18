package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.SpellSoundRegistry;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.hollingsworth.arsnouveau.client.gui.BookSlider;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.SoundButton;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSetSound;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateSpellSoundAll;
import com.hollingsworth.arsnouveau.setup.registry.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;

import java.util.List;

import static com.hollingsworth.arsnouveau.ArsNouveau.prefix;

public class SoundScreen extends BaseBook {

    public int casterSlot;
    public InteractionHand stackHand;

    public SoundScreen(ConfiguredSpellSound configuredSpellSound, int slot, InteractionHand stackHand, Screen parent) {
        super();
        volume = configuredSpellSound.getVolume() * 100;
        pitch = configuredSpellSound.getPitch() * 100;
        selectedSound = configuredSpellSound.getSound();
        casterSlot = slot;
        this.parent = parent;
        this.stackHand = stackHand;
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

        addRenderableWidget(new GuiImageButton(bookRight - 71, bookBottom - 12, 0, 0, 41, 12, 41, 12, "textures/gui/clear_icon.png", (e) -> Minecraft.getInstance().setScreen(parent)));

        volumeSlider = buildSlider(bookLeft + 28, bookTop + 49, Component.translatable("ars_nouveau.sounds.volume"), Component.empty(), volume);
        pitchSlider = buildSlider(bookLeft + 28, bookTop + 89, Component.translatable("ars_nouveau.sounds.pitch"), Component.empty(), pitch);

        addRenderableWidget(volumeSlider);
        addRenderableWidget(pitchSlider);
        addRenderableWidget(new GuiImageButton(bookLeft + 25, bookBottom - 36, 0, 0, 37, 12, 37, 12, "textures/gui/save_icon.png", this::onSaveClick));
        addRenderableWidget(new GuiImageButton(bookLeft + 165, bookBottom - 36, 0, 0, 37, 12, 37, 12, "textures/gui/save_icon.png", this::onSaveAllClick));
        GuiImageButton testButton = new GuiImageButton(bookLeft + 90, bookBottom - 36, 0, 0, 37, 12, 37, 12, "textures/gui/sound_test_icon.png", this::onTestClick);
        testButton.soundDisabled = true;
        addRenderableWidget(testButton);

        selectedButton = new SoundButton(bookLeft + 69, bookTop + 131, selectedSound, (b) -> {
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
        List<SpellSound> sounds = SpellSoundRegistry.getSpellSounds();
        for (SpellSound part : sounds) {
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

            SoundButton cell = new SoundButton(xStart + xOffset, yPlace, part, this::onSoundClick);
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
        localPlayer.level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), selectedSound.getSoundEvent().value(), SoundSource.PLAYERS, (float) volumeSlider.getValue() / 100f, (float) pitchSlider.getValue() / 100f, false);
    }

    public void onSaveClick(Button button) {
        Networking.sendToServer(new PacketSetSound(casterSlot, selectedSound == null ? ConfiguredSpellSound.EMPTY : new ConfiguredSpellSound(selectedSound, (float) volumeSlider.getValue() / 100f, (float) pitchSlider.getValue() / 100f), stackHand == InteractionHand.MAIN_HAND));
    }

    public void onSaveAllClick(Button button) {
        Networking.sendToServer(new PacketUpdateSpellSoundAll(casterSlot, selectedSound == null ? ConfiguredSpellSound.EMPTY : new ConfiguredSpellSound(selectedSound, (float) volumeSlider.getValue() / 100f, (float) pitchSlider.getValue() / 100f), stackHand == InteractionHand.MAIN_HAND));
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(ArsNouveau.prefix( "textures/gui/sound_slider_gilding.png"), 22, 47, 0, 0, 112, 104, 112, 104);
        int color = -8355712;
        graphics.drawString(font, Component.translatable("ars_nouveau.sounds.title").getString(), 51, 24, color, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.save").getString(), 37, 160, color, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.color_gui.save_all").getString(), 177, 160, color, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.sounds.test").getString(), 102, 160, color, false);
        graphics.blit(prefix("textures/gui/create_paper.png"), 216, 175, 0, 0, 56, 15, 56, 15);
        graphics.drawString(font, Component.translatable("ars_nouveau.spell_book_gui.close"), 238, 180, color, false);
    }
}
