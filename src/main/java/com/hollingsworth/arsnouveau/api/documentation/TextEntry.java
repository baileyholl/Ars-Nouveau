package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.nuggets.client.gui.GuiHelpers;
import com.hollingsworth.nuggets.client.gui.NuggetMultilLineLabel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class TextEntry extends SinglePageWidget {
    Component body;
    @Nullable Component title;
    @Nullable ItemStack renderStack;
    NuggetMultilLineLabel titleLabel;
    public TextEntry(Component body, Component title, ItemStack renderStack, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.body = body;
        this.title = title;
        this.renderStack = renderStack;
        if(title != null){
            this.titleLabel = NuggetMultilLineLabel.create(Minecraft.getInstance().font, title, 100);
        }
    }

    public static SinglePageCtor create(Component body, Component title, ItemStack renderStack){
        return (parent, x, y, width, height) -> new TextEntry(body, title, renderStack, parent, x, y, width, height);
    }

    public static SinglePageCtor create(Component body, Component title){
        return (parent, x, y, width, height) -> new TextEntry(body, title, null, parent, x, y, width, height);
    }

    public static SinglePageCtor create(Component body){
        return (parent, x, y, width, height) -> new TextEntry(body, null, null, parent, x, y, width, height);
    }

    public int drawTitle(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks){
        Font font = Minecraft.getInstance().font;
        if(renderStack != null){
            DocClientUtils.blit(guiGraphics, DocAssets.HEADER_WITH_ITEM, x, y);
            setTooltipIfHovered(DocClientUtils.renderItemStack(guiGraphics, x + 3, y + 3, mouseX, mouseY, renderStack));
            DocClientUtils.drawHeader(titleLabel, guiGraphics, x + 70, y);
            return 28;
        }else{
            DocClientUtils.blit(guiGraphics, DocAssets.UNDERLINE, x + 2, y + 10);
            GuiHelpers.drawCenteredStringNoShadow(font, guiGraphics, title, x + 60, y, 0);
        }
        return 20;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        boolean hasTitle = title != null;
        Font font = Minecraft.getInstance().font;
        int yOffset = 0;
        if(hasTitle){
           yOffset = drawTitle(guiGraphics, mouseX, mouseY, partialTick);
        }

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        float scale = 0.75f;
        poseStack.translate(x + 3, y + yOffset, 0);
        poseStack.scale(scale, scale, 1);
        NuggetMultilLineLabel label = NuggetMultilLineLabel.create(Minecraft.getInstance().font, body, (int) (width * 1.185));
        label.renderLeftAlignedNoShadow(guiGraphics, 0, 0, 12, 0);

//        float dist = 0.08F;
//        for(int cycle = 0; cycle < 2; cycle++){
//            poseStack.translate(-dist, 0F, 0F);
//            label.renderLeftAlignedNoShadow(guiGraphics, 0, 0, 12, 0);
//            poseStack.translate(dist, -dist, 0F);
//            label.renderLeftAlignedNoShadow(guiGraphics, 0, 0, 12, 0);
//            poseStack.translate(dist, 0F, 0F);
//            label.renderLeftAlignedNoShadow(guiGraphics, 0, 0, 12, 0);
//            poseStack.translate(-dist, dist, 0F);
//
//            dist = -dist;
//        }
        poseStack.popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
