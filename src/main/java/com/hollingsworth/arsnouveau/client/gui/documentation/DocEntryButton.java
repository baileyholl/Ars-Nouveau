package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.client.gui.buttons.SelectableButton;
import com.hollingsworth.nuggets.client.rendering.RenderHelpers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class DocEntryButton extends SelectableButton {
    public ItemStack renderStack;
    public Component title;

    public DocEntryButton(int x, int y, ItemStack renderStack, Component display, OnPress onPress) {
        super(x, y, DocAssets.DOC_ENTRY_BUTTON, DocAssets.DOC_ENTRY_BUTTON_SELECTED, onPress);
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
        int xOffset = 14;
        if(renderStack.isEmpty()){
            xOffset = 2;
            DocClientUtils.blit(graphics, DocAssets.CHAPTER_BUTTON_NO_ITEM, x, y);
        }else {
            super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        }
        RenderHelpers.drawItemAsIcon(renderStack, graphics, x - 1, y - 1 , 10, false);
        DocClientUtils.drawStringScaled(graphics, title, x + xOffset, y + 3, 0, 0.8f, false);
    }
}
