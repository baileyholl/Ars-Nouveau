package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.nuggets.client.gui.NuggetMultilLineLabel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class TextEntry extends SinglePageWidget {
    String text;

    public TextEntry(String text, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.text = text;
    }

    public static SinglePageCtor create(String text){
        return (parent, x, y, width, height) -> new TextEntry(text, parent, x, y, width, height);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        float scale = 0.75f;
        poseStack.translate(x + 3, y, 1000);
        poseStack.scale(scale, scale, scale);
        NuggetMultilLineLabel label = NuggetMultilLineLabel.create(Minecraft.getInstance().font, Component.literal(text), (int) (width * 1.185));
        label.renderLeftAlignedNoShadow(guiGraphics, 0, 0, 12, 0);
        poseStack.popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
