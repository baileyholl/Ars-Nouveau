package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.nuggets.client.gui.NuggetImageButton;
import com.hollingsworth.nuggets.client.rendering.RenderHelpers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ParticleOptionButton extends NuggetImageButton {
    public Component label;
    public ItemStack renderStack;

    public ParticleOptionButton(int x, int y, OnPress onPress, Component title, ItemStack renderStack) {
        super(x, y, DocAssets.DOC_ENTRY_BUTTON.width(), DocAssets.DOC_ENTRY_BUTTON.height(), DocAssets.DOC_ENTRY_BUTTON.location(), onPress);
        this.label = title;
        this.renderStack = renderStack;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        RenderHelpers.drawItemAsIcon(renderStack, graphics, x - 1, y - 1 , 10, false);
        DocClientUtils.drawStringScaled(graphics, label, x + 14, y + 4, 0, 0.8f, false);
    }
}
