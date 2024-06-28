package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;

import java.util.function.Supplier;

public class CreateSpellButton extends GuiImageButton {

    public Supplier<Boolean> shouldRenderRed;

    public CreateSpellButton(int x, int y, Button.OnPress onPress, Supplier<Boolean> renderRed) {
        super(x, y, 0, 0, 50, 12, 50, 12, "textures/gui/create_icon.png", onPress);
        this.shouldRenderRed = renderRed;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (!shouldRenderRed.get()) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            RenderSystem.setShaderColor(1.0F, 0.7F, 0.7F, 1.0F);
        }
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
    }
}
