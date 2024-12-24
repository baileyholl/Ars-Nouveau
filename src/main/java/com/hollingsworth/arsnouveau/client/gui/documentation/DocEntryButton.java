package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.nuggets.client.gui.NuggetImageButton;
import com.hollingsworth.nuggets.client.rendering.RenderHelpers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class DocEntryButton extends NuggetImageButton {
    public static ResourceLocation image = ArsNouveau.prefix("textures/gui/documentation/doc_button_chapter.png");
    DocEntry docEntry;

    public DocEntryButton(int x, int y, DocEntry docEntry, OnPress onPress) {
        super(x, y, 118, 14, image, onPress);
        this.docEntry = docEntry;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        RenderHelpers.drawItemAsIcon(docEntry.renderStack(),graphics, x - 1, y - 1 , 10, false);
        DocClientUtils.drawStringScaled(graphics, docEntry.entryTitle(), x + 14, y + 4, 0, 0.8f, false);
    }
}
