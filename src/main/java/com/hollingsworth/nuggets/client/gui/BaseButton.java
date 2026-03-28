package com.hollingsworth.nuggets.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BaseButton extends Button.Plain implements ITooltipRenderer {
    public boolean playSound = true;
    public List<Component> tooltips = new ArrayList<>();

    public BaseButton(int x, int y, int w, int h, @NotNull Component text, OnPress onPress) {
        super(x, y, w, h, text, onPress, Button.DEFAULT_NARRATION);
    }

    public BaseButton(int x, int y, int width, int height, Component message, OnPress onPress, CreateNarration createNarration) {
        super(x, y, width, height, message, onPress, createNarration);
    }

    public BaseButton setPlaySound(boolean playSound) {
        this.playSound = playSound;
        return this;
    }

    public BaseButton withTooltips(List<Component> tooltips){
        this.tooltips = tooltips;
        return this;
    }

    public BaseButton withTooltip(Component tooltip){
        this.tooltips.add(tooltip);
        return this;
    }

    @Override
    public void gatherTooltips(List<Component> tooltip) {
        tooltip.addAll(tooltips);
    }

    @Override
    public void playDownSound(SoundManager pHandler) {
        if(playSound){
            super.playDownSound(pHandler);
        }
    }
}
