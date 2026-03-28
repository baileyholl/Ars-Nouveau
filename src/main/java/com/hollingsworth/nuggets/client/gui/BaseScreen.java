package com.hollingsworth.nuggets.client.gui;

import com.hollingsworth.arsnouveau.common.mixin.ScreenAccessor;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BaseScreen extends Screen {

    public int maxScale;
    public float scaleFactor;
    public int screenLeft;
    public int screenTop;
    public int screenRight;
    public int screenBottom;

    public int fullWidth;
    public int fullHeight;

    public Identifier background;

    public BaseScreen(Component titleIn, int fullWidth, int fullHeight, Identifier background) {
        super(titleIn);
        this.fullHeight = fullHeight;
        this.fullWidth = fullWidth;
        this.background = background;
    }

    @Override
    public void init() {
        super.init();
        this.maxScale = this.getMaxAllowedScale();
        this.scaleFactor = 1.0F;
        screenLeft = width / 2 - fullWidth / 2;
        screenTop = height / 2 - fullHeight / 2;
        screenRight = width / 2 + fullWidth / 2;
        screenBottom = height / 2 + fullHeight / 2;
    }

    public void drawTooltip(GuiGraphics stack, int mouseX, int mouseY) {
        List<Component> tooltip = new ArrayList<>();
        collectTooltips(stack, mouseX, mouseY, tooltip);
        if (!tooltip.isEmpty()) {
            stack.setTooltipForNextFrame(font, tooltip, Optional.ofNullable(getClientImageTooltip(mouseX, mouseY)), mouseX, mouseY);
        }
    }

    public void collectTooltips(GuiGraphics stack, int mouseX, int mouseY, List<Component> tooltip){
        for(Renderable renderable : renderablesList()){
            if(renderable instanceof AbstractWidget widget && renderable instanceof ITooltipRenderer tooltipProvider){
                if(GuiHelpers.isMouseInRelativeRange(mouseX, mouseY, widget) && widget.visible){
                    tooltipProvider.gatherTooltips(stack, mouseX, mouseY, tooltip);
                }
            }
        }
    }

    protected TooltipComponent getClientImageTooltip(int mouseX, int mouseY) {
        return null;
    }

    public void drawForegroundElements(int mouseX, int mouseY, float partialTicks) {
    }

    public void drawScreenAfterScale(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // MC 1.21.11: renderBackground() (and its blurBeforeThisStratum call) is invoked by the
        // framework BEFORE render() is called, so calling it again here would throw
        // "Can only blur once per frame". Background drawing is handled in drawBackgroundElements().
        var poseStack = graphics.pose();
        poseStack.pushMatrix();
        poseStack.translate(screenLeft, screenTop);
        drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        drawForegroundElements(mouseX, mouseY, partialTicks);
        poseStack.popMatrix();
        for (Renderable renderable : this.renderablesList()) {
            renderable.render(graphics, mouseX, mouseY, partialTicks);
        }
        drawTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        var matrixStack = graphics.pose();
        matrixStack.pushMatrix();
        if (scaleFactor != 1) {
            matrixStack.scale(scaleFactor, scaleFactor);
            mouseX /= scaleFactor;
            mouseY /= scaleFactor;
        }
        drawScreenAfterScale(graphics, mouseX, mouseY, partialTicks);
        matrixStack.popMatrix();
    }

    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, background, 0, 0, 0.0f, 0.0f, fullWidth, fullHeight, fullWidth, fullHeight);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int getMaxAllowedScale() {
        return this.minecraft.getWindow().calculateScale(0, this.minecraft.isEnforceUnicode());
    }

    public List<Renderable> renderablesList() {
        return ((ScreenAccessor)this).getRenderables();
    }

    protected Map<NestedWidgets, List<AbstractWidget>> nestedWidgetMap = new Reference2ObjectOpenHashMap<>();

    @Override
    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
        List<AbstractWidget> afterParent = new ArrayList<>();
        List<AbstractWidget> beforeParent = new ArrayList<>();

        if (widget instanceof NestedWidgets nestedRenderables) {
            nestedRenderables.addBeforeParent(beforeParent);

            for (AbstractWidget renderable : beforeParent) {
                super.addRenderableWidget(renderable);
            }

            nestedRenderables.addAfterParent(afterParent);
            List<AbstractWidget> allWidgets = new ArrayList<>();
            allWidgets.addAll(afterParent);
            allWidgets.addAll(beforeParent);
            nestedWidgetMap.put(nestedRenderables, allWidgets);
        }
        T added = super.addRenderableWidget(widget);
        for(AbstractWidget renderable : afterParent) {
            super.addRenderableWidget(renderable);
        }
        return added;
    }

    @Override
    protected void removeWidget(GuiEventListener pListener) {
        if(pListener instanceof NestedWidgets nestedWidgets){
            var children = nestedWidgetMap.get(nestedWidgets);
            if(children != null){
                for(AbstractWidget renderable : children){
                    if(renderable != null){
                        super.removeWidget(renderable);
                    }
                }
            }
            nestedWidgetMap.remove(nestedWidgets);
        }
        super.removeWidget(pListener);
    }
}
