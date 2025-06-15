package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.client.gui.buttons.SelectableButton;
import com.hollingsworth.nuggets.client.gui.NestedWidgets;
import com.hollingsworth.nuggets.client.rendering.RenderHelpers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.function.Supplier;

public class DocEntryButton extends SelectableButton implements NestedWidgets {
    public ItemStack renderStack;
    public Component title;
    public DocAssets.BlitInfo icon;
    public Component fullTitle;

    public SelectableButton favoriteButton;

    protected Button.OnPress onFavorited;
    protected Supplier<Boolean> isFavorited;
    public TriFunction<Double, Double, Integer, Boolean> onClickFunction;

    public DocEntryButton(int x, int y, ItemStack renderStack, Component display, OnPress onPress) {
        super(x, y, DocAssets.DOC_ENTRY_BUTTON, DocAssets.DOC_ENTRY_BUTTON_SELECTED, onPress);
        this.renderStack = renderStack;
        this.title = display;
        int length = 25;
        String displayString = display.getString();
        if(display.getString().length() > length + 3) {
            this.fullTitle = display;
            displayString = display.getString().substring(0, length + 1).trim() + "...";
        }
        this.title = Component.literal(displayString);
    }

    public DocEntryButton(int x, int y, DocEntry docEntry, OnPress onPress) {
        this(x, y, docEntry.renderStack(), docEntry.entryTitle(), onPress);
    }

    public DocEntryButton withStaticIcon(DocAssets.BlitInfo blitInfo) {
        this.icon = blitInfo;
        return this;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if(favoriteButton != null) {
            if(!visible || !active) {
                favoriteButton.visible = true;
                favoriteButton.active = true;
                favoriteButton.x = -999999;
                favoriteButton.y = -999999;
            }else{
                if (isFavorited.get() || this.isHovered()) {
                    favoriteButton.visible = true;
                    favoriteButton.active = true;
                    favoriteButton.x = x + width - 10;
                    favoriteButton.y = y + 4;
                } else {
                    favoriteButton.visible = false;
                    favoriteButton.active = false;
                    favoriteButton.x = -999999;
                    favoriteButton.y = -999999;
                }
            }
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {

        int xOffset = 14;
        if(renderStack.isEmpty() && icon == null){
            xOffset = 2;
            DocClientUtils.blit(graphics, DocAssets.CHAPTER_BUTTON_NO_ITEM, x, y);
        }else {
            super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        }
        if(icon != null) {
            graphics.blit(icon.location(), x + 1, y + 1, 0, 0, 12, 12, 12, 12);
        }
        RenderHelpers.drawItemAsIcon(renderStack, graphics, x - 1, y - 1 , 10, false);
        DocClientUtils.drawStringScaled(graphics, title, x + xOffset, y + 3, 0, 0.8f, false);
    }


    @Override
    protected boolean isValidClickButton(int button) {
        return super.isValidClickButton(button) || button == 1;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(onClickFunction != null && onClickFunction.apply(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(onClickFunction != null && onClickFunction.apply(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (onClickFunction == null || !onClickFunction.apply(mouseX, mouseY, button)) {
            super.onClick(mouseX, mouseY, button);
        }
    }

    @Override
    public void addBeforeParent(List<AbstractWidget> widgets) {
        if(onFavorited != null) {
            favoriteButton = new SelectableButton(0, 0, DocAssets.FAVORITE_ICON_HOVER, DocAssets.FAVORITE_ICON, (b) -> {
                onFavorited.onPress(b);
                favoriteButton.isSelected = isFavorited.get();
            });
            favoriteButton.isSelected = isFavorited.get();
            widgets.add(favoriteButton);
        }
    }

    public DocEntryButton setFavoritable(Supplier<Boolean> isFavorited, Button.OnPress onFavorited){
        this.onFavorited = onFavorited;
        this.isFavorited = isFavorited;
        return this;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if(fullTitle != null){
            tooltip.add(fullTitle);
        }
        super.getTooltip(tooltip);
    }
}
