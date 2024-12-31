package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.nuggets.client.gui.NuggetImageButton;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.List;

public class BookmarkButton extends NuggetImageButton {

    DocEntry entry;
    public BookmarkButton(int x, int y, DocEntry entry, OnPress onPress) {
        super(x, y, DocAssets.BOOKMARK.width(), DocAssets.BOOKMARK.height(), DocAssets.BOOKMARK.location(), onPress);
        this.entry = entry;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        if(entry != null){
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.translate(x - 1, y - 1.5f, 0);
            RenderUtils.drawItemAsIcon(entry.renderStack(), graphics, 0 , 0, 8, false);
            poseStack.popPose();
        }
    }

    @Override
    public void gatherTooltips(List<Component> tooltip) {
        super.gatherTooltips(tooltip);
        if(entry != null){
            tooltip.add(entry.entryTitle());
            tooltip.add(Component.translatable("ars_nouveau.shift_delete").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        }
    }
}
