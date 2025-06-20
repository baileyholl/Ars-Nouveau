package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.client.gui.BookSlider;
import com.hollingsworth.arsnouveau.client.gui.GuiUtils;
import com.hollingsworth.arsnouveau.client.gui.buttons.ANButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.SaveButton;
import com.hollingsworth.nuggets.client.gui.BaseScreen;
import com.hollingsworth.nuggets.client.gui.ITooltipRenderer;
import com.hollingsworth.nuggets.client.gui.NuggetImageButton;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BaseBook extends BaseScreen {
    public static final int FONT_COLOR = -8355712;
    public static final int FULL_WIDTH = 290;
    public static final int FULL_HEIGHT = 188;
    public static final int LEFT_PAGE_OFFSET = 19;
    public static final int RIGHT_PAGE_OFFSET = 153;
    public static final int PAGE_TOP_OFFSET = 17;
    public static final int ONE_PAGE_WIDTH = 118;
    public static final int ONE_PAGE_HEIGHT = 146;

    public static ResourceLocation background = ArsNouveau.prefix( "textures/gui/spell_book_template.png");
    public int bookLeft;
    public int bookTop;
    public int bookRight;
    public int bookBottom;
    public List<SpellValidationError> validationErrors = new ArrayList<>();
    public SaveButton saveButton;
    public static BaseBook lastOpenedScreen = null;

    public BaseBook() {
        super(Component.literal(""), DocAssets.BACKGROUND.width(), DocAssets.BACKGROUND.height(), DocAssets.BACKGROUND.location());
    }

    @Override
    public void init() {
        super.init();
        BaseBook.lastOpenedScreen = this;
        bookLeft = width / 2 - FULL_WIDTH / 2;
        bookTop = height / 2 - FULL_HEIGHT / 2;
        bookRight = width / 2 + FULL_WIDTH / 2;
        bookBottom = height / 2 + FULL_HEIGHT / 2;
    }

    public void addBackButton(Screen parentScreen){
        addBackButton(parentScreen,(b) ->{});
    }


    public void addBackButton(Screen parentScreen, Consumer<Button> onPress){
        addRenderableWidget(new NuggetImageButton(bookLeft + 6, bookTop + 6, DocAssets.ARROW_BACK_HOVER.width(), DocAssets.ARROW_BACK_HOVER.height(), DocAssets.ARROW_BACK.location(), DocAssets.ARROW_BACK_HOVER.location(), (b) -> {
            if (onPress != null) {
                onPress.accept(b);
            }
            Minecraft.getInstance().setScreen(parentScreen);
        }));
    }

    public void addSaveButton(Button.OnPress onPress) {
        saveButton = addRenderableWidget(new SaveButton(bookRight - DocAssets.SAVE_ICON.width() - 18, bookBottom - DocAssets.SAVE_ICON.height() + 2, onPress));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        if (scaleFactor != 1) {
            matrixStack.scale(scaleFactor, scaleFactor, scaleFactor);
            mouseX /= scaleFactor;
            mouseY /= scaleFactor;
        }
        drawScreenAfterScale(graphics, mouseX, mouseY, partialTicks);
        matrixStack.popPose();
    }

    public <T extends ANButton> void clearButtons(List<T> buttons) {
        for (ANButton b : buttons) {
            renderables.remove(b);
            children().remove(b);
        }
        buttons.clear();
    }

    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(background, 0, 0, 0, 0, FULL_WIDTH, FULL_HEIGHT, FULL_WIDTH, FULL_HEIGHT);
    }

    public void drawForegroundElements(int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void drawScreenAfterScale(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(bookLeft, bookTop, 0);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        drawForegroundElements(mouseX, mouseY, partialTicks);
        poseStack.popPose();
        for (Renderable renderable : this.renderables) {
            renderable.render(graphics, mouseX, mouseY, partialTicks);
        }
        drawTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void collectTooltips(GuiGraphics stack, int mouseX, int mouseY, List<Component> tooltip) {
        // TODO: 1.22 remove along with ITooltipProvider from ars
        for(Renderable renderable : renderables) {
            if (renderable instanceof AbstractWidget widget) {
                if (GuiUtils.isMouseInRelativeRange(mouseX, mouseY, widget)) {
                    if (renderable instanceof ITooltipProvider tooltipProvider) {
                        tooltipProvider.getTooltip(tooltip);
                    }else if(renderable instanceof ITooltipRenderer nuggetProvider){
                        nuggetProvider.gatherTooltips(tooltip);
                    }
                }
            }
        }
    }

    public @Nullable Renderable getHoveredRenderable(int mouseX, int mouseY){
        for(Renderable renderable : renderables){
            if(renderable instanceof AbstractWidget widget){
                if(GuiUtils.isMouseInRelativeRange(mouseX, mouseY, widget)){
                    return renderable;
                }
            }
        }
        return null;
    }

    public BookSlider buildSlider(int x, int y, Component prefix, Component suffix, double currentVal) {
        return new BookSlider(x, y, 100, 20, prefix, suffix, 1.0D, 255.0D, currentVal, 1, 1, true);
    }

    @Override
    protected void renderBlurredBackground(float pPartialTick) {

    }
}
