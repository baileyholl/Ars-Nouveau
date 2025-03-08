package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.nuggets.client.gui.NuggetImageButton;
import com.hollingsworth.nuggets.client.gui.NuggetMultilLineLabel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ParticleOptionButton extends NuggetImageButton {
    public NuggetMultilLineLabel label;
    public ParticleOptionButton(int x, int y, OnPress onPress, Component title) {
        super(x, y, DocAssets.HEADER_WITH_ITEM.width(), DocAssets.HEADER_WITH_ITEM.height(), DocAssets.HEADER_WITH_ITEM.location(), onPress);
        this.label = NuggetMultilLineLabel.create(Minecraft.getInstance().font, title, 98);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        DocClientUtils.drawHeader(label, graphics, x + 68, y);
    }
}
