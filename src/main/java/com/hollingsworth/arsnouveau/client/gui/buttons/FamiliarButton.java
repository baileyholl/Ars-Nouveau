package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class FamiliarButton extends ANButton {

    public AbstractFamiliarHolder familiarHolder;

    public FamiliarButton(int x, int y, AbstractFamiliarHolder familiar, OnPress onPress) {
        super(x, y, 16, 16,  onPress);
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 16;
        this.familiarHolder = familiar;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            RenderUtil.drawItemAsIcon(familiarHolder.getOutputItem(), graphics, x, y, 16, false);
        }
    }

    @Override
    public void getTooltip(List<Component> tip) {
        if (Screen.hasShiftDown()) {
            tip.add(familiarHolder.getLangDescription());
        } else {
            tip.add(familiarHolder.getLangName());
            tip.add(Component.translatable("tooltip.ars_nouveau.hold_shift", Minecraft.getInstance().options.keyShift.getKey().getDisplayName()));
        }
    }
}