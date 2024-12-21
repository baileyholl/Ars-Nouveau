package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.nuggets.client.gui.GuiHelpers;
import com.hollingsworth.nuggets.client.gui.ITooltipRenderer;
import com.hollingsworth.nuggets.client.gui.NestedWidgets;
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
}
