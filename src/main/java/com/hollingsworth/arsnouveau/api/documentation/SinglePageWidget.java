package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.nuggets.client.gui.GuiHelpers;
import com.hollingsworth.nuggets.client.gui.ITooltipRenderer;
import com.hollingsworth.nuggets.client.gui.NestedWidgets;
import com.hollingsworth.nuggets.client.gui.NuggetMultilLineLabel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SinglePageWidget extends AbstractWidget implements NestedWidgets, ITooltipRenderer {
    public ItemStack tooltipStack = ItemStack.EMPTY;
    public BaseDocScreen parent;

    public SinglePageWidget(BaseDocScreen parent, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.parent = parent;
    }

    public List<AbstractWidget> getExtras(){
        return new ArrayList<>();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        tooltipStack = ItemStack.EMPTY;
    }

    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick){

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public void playDownSound(SoundManager handler) {

    }

    public void setTooltipIfHovered(ItemStack stack){
        if(stack.isEmpty()){
            return;
        }
        this.tooltipStack = stack;
    }

    @Override
    public void gatherTooltips(List<Component> list) {
        if (!tooltipStack.isEmpty()) {
            list.addAll(tooltipStack.getTooltipLines(Item.TooltipContext.EMPTY, null, TooltipFlag.NORMAL));
        }
    }

    public void drawHeader(@Nullable Component title, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        DocClientUtils.blit(guiGraphics, DocAssets.UNDERLINE, x + 11, y + 9);
        if(title != null) {
            GuiHelpers.drawCenteredStringNoShadow(Minecraft.getInstance().font, guiGraphics, title, x + width / 2, y, 0);
        }
    }

    public void drawParagraph(Component text, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTick) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        float scale = 0.70f;
        poseStack.translate(x + 5, y, 0);
        poseStack.scale(scale, scale, 1);
        NuggetMultilLineLabel label = NuggetMultilLineLabel.create(Minecraft.getInstance().font, text, (int) (width * 1.155));
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
}
