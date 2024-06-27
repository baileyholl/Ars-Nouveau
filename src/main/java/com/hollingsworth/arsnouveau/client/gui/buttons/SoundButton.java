package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SoundButton extends ANButton {

    public SpellSound sound;

    public SoundButton(int x, int y, SpellSound sound, Button.OnPress onPress) {
        super(x, y, 16, 16, onPress);
        this.sound = sound;
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.blit(sound.getTexturePath(), x, y, 0, 0, 16, 16, 16, 16);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (sound != null)
            tooltip.add(sound.getSoundName());
    }
}
