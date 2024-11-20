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
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.5f, 0.5f, 0.5f);
        poseStack.translate(x, y, 1000);
        NuggetMultilLineLabel label = NuggetMultilLineLabel.create(Minecraft.getInstance().font, Component.literal(text), width * 2 - 20);
        label.renderLeftAlignedNoShadow(guiGraphics, x, y, 12, 0);
//        guiGraphics.drawString(Minecraft.getInstance().font, text, x, y, 0, false);
        guiGraphics.pose().popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
