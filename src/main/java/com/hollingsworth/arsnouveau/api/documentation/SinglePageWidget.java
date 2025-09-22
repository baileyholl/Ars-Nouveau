package com.hollingsworth.arsnouveau.api.documentation;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.documentation.export.IJsonExportable;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.nuggets.client.gui.ITooltipRenderer;
import com.hollingsworth.nuggets.client.gui.NestedWidgets;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;

public class SinglePageWidget extends AbstractWidget implements NestedWidgets, ITooltipRenderer, IJsonExportable {
    public ItemStack tooltipStack = ItemStack.EMPTY;
    public BaseDocScreen parent;

    public SinglePageWidget(BaseDocScreen parent, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.parent = parent;
    }

    public List<AbstractWidget> getExtras() {
        return new ArrayList<>();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        tooltipStack = ItemStack.EMPTY;
    }

    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

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

    public void setTooltipIfHovered(ItemStack stack) {
        if (stack.isEmpty()) {
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

    @Override
    public void gatherTooltips(GuiGraphics stack, int mouseX, int mouseY, List<Component> tooltip) {
        if (!tooltipStack.isEmpty()) {
            tooltip.addAll(tooltipStack.getTooltipLines(Item.TooltipContext.EMPTY, null, TooltipFlag.NORMAL));
            List<Either<FormattedText, TooltipComponent>> components = new ArrayList<>();
            NeoForge.EVENT_BUS.post(new RenderTooltipEvent.GatherComponents(tooltipStack, width, height, components, width));
            components.forEach(component -> {
                component.left().ifPresent(left -> {
                    tooltip.add(Component.literal(left.getString()));
                });
            });
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        addExportProperties(object);
        return object;
    }

    public void addExportProperties(JsonObject object) {

    }
}
