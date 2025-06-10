package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.client.gui.buttons.SelectableButton;
import com.hollingsworth.nuggets.client.rendering.RenderHelpers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DocEntryButton extends SelectableButton {
    public ItemStack renderStack;
    public Component title;
    public DocAssets.BlitInfo icon;
    public Component fullTitle;

    public DocEntryButton(int x, int y, ItemStack renderStack, Component display, OnPress onPress) {
        super(x, y, DocAssets.DOC_ENTRY_BUTTON, DocAssets.DOC_ENTRY_BUTTON_SELECTED, onPress);
        this.renderStack = renderStack;
        this.title = display;
        int length = 25;
        display = Component.literal(display.getString() + display.getString());
        String displayString = display.getString();
        if(display.getString().length() > length + 3) {
            this.fullTitle = display;
            displayString = display.getString().substring(0, length + 1).trim() + "...";
        }
        this.title = Component.literal(displayString);
    }

    public DocEntryButton(int x, int y, DocEntry docEntry, OnPress onPress) {
        this(x, y, docEntry.renderStack(), docEntry.entryTitle(), onPress);
    }

    public DocEntryButton withStaticIcon(DocAssets.BlitInfo blitInfo) {
        this.icon = blitInfo;
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if(!visible){
            return;
        }
        int xOffset = 14;
        if(renderStack.isEmpty() && icon == null){
            xOffset = 2;
            DocClientUtils.blit(graphics, DocAssets.CHAPTER_BUTTON_NO_ITEM, x, y);
        }else {
            super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        }
        if(icon != null) {
            graphics.blit(icon.location(), x + 1, y + 1, 0, 0, 12, 12, 12, 12);
        }
        RenderHelpers.drawItemAsIcon(renderStack, graphics, x - 1, y - 1 , 10, false);
        DocClientUtils.drawStringScaled(graphics, title, x + xOffset, y + 3, 0, 0.8f, false);
    }


    @Override
    public void getTooltip(List<Component> tooltip) {
        if(fullTitle != null){
            tooltip.add(fullTitle);
        }
        super.getTooltip(tooltip);
    }
}
