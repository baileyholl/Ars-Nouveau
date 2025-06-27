package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ANButton extends Button implements ITooltipProvider {

    public ANButton(int x, int y, int w, int h, @NotNull Component text, OnPress onPress) {
        super(x, y, w, h, text, onPress, Button.DEFAULT_NARRATION);
    }

    public ANButton(int x, int y, int w, int h, OnPress onPress) {
        this(x, y, w, h, Component.empty(), onPress);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
    }
}
