package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.nuggets.client.gui.NuggetImageButton;
import com.hollingsworth.nuggets.client.rendering.RenderHelpers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class DocEntryButton extends NuggetImageButton {
    public ItemStack renderStack;
    public Component title;


    public DocEntryButton(int x, int y, ItemStack renderStack, Component display, OnPress onPress) {
        super(x, y, DocAssets.DOC_ENTRY_BUTTON.width(), DocAssets.DOC_ENTRY_BUTTON.height(), DocAssets.DOC_ENTRY_BUTTON.location(), onPress);
        this.renderStack = renderStack;
        this.title = display;
    }

    public DocEntryButton(int x, int y, DocEntry docEntry, OnPress onPress) {
        this(x, y, docEntry.renderStack(), docEntry.entryTitle(), onPress);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if(!visible){
            return;
        }
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        RenderHelpers.drawItemAsIcon(renderStack, graphics, x - 1, y - 1 , 10, false);
        DocClientUtils.drawStringScaled(graphics, title, x + 14, y + 3, 0, 0.8f, false);
    }
}
