package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.api.documentation.DocPlayerData;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageWidget;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class PageHolderScreen extends BaseDocScreen{

    List<SinglePageWidget> allWidgets = new ArrayList<>();

    public SinglePageWidget leftPage = null;

    public SinglePageWidget rightPage = null;
    List<SinglePageCtor> pages;
    public PageHolderScreen(List<SinglePageCtor> pages) {
        super();
        this.pages = new ArrayList<>(pages);
        this.maxArrowIndex = (pages.size() - 1) / 2;
    }

    @Override
    public void init() {
        super.init();
        allWidgets = new ArrayList<>();
        for(int i = 0; i < pages.size(); i++){
            SinglePageCtor page = pages.get(i);
            SinglePageWidget widget = page.create(this,  (i + 1) % 2 == 0 ? bookLeft + 150 : bookLeft + 16, bookTop + 24, 135, 180);
            allWidgets.add(widget);
        }
        initPages();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        if(leftPage != null){
            leftPage.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        }
        if(rightPage != null){
            rightPage.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void onArrowIndexChange() {
        super.onArrowIndexChange();
        DocPlayerData.lastOpenedPage = arrowIndex;
        this.rebuildWidgets();
    }

    public void initPages(){
        if(leftPage != null){
            removeWidget(leftPage);
        }
        if(rightPage != null){
            removeWidget(rightPage);
        }
        leftPage = null;
        rightPage = null;
        if(arrowIndex * 2 < allWidgets.size()){
            leftPage = allWidgets.get(arrowIndex * 2);
            addRenderableWidget(leftPage);
        }
        if(arrowIndex * 2 + 1 < allWidgets.size()){
            rightPage = allWidgets.get(arrowIndex * 2 + 1);
            addRenderableWidget(rightPage);
        }
    }
}
