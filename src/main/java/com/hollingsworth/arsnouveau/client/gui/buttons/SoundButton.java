package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.book.SoundScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class SoundButton extends Button {

    SoundScreen parent;
    public SpellSound sound;

    public SoundButton(SoundScreen parent, int x, int y, SpellSound sound, Button.OnPress onPress) {
        super(x, y, 16, 16, Component.nullToEmpty(""), onPress);
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 16;
        this.sound = sound;
    }

    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        if (visible && sound != null) {
            GuiSpellBook.drawFromTexture(sound.getTexturePath(), x, y, 0, 0, 16, 16, 16, 16, ms);
            if (parent.isMouseInRelativeRange(mouseX, mouseY, x, y, width, height)) {
                List<Component> tip = new ArrayList<>();
                tip.add(sound.getSoundName());
                parent.tooltip = tip;
            }

        }
    }
}
