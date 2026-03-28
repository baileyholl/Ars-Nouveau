package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import net.minecraft.client.gui.GuiGraphics;
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

    // 1.21.11: Button is now abstract; renderContents replaces the old renderWidget override.
    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Default: draw button label centered. Subclasses that need custom rendering override this.
        Component message = getMessage();
        if (!message.getString().isEmpty()) {
            guiGraphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font, message, getX() + width / 2, getY() + (height - 8) / 2, 0xFFFFFFFF);
        }
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
    }
}
