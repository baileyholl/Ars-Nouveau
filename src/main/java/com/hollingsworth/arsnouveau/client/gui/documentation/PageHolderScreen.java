package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocPlayerData;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageWidget;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.nuggets.client.gui.NuggetImageButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PageHolderScreen extends BaseDocScreen{

    List<SinglePageWidget> allWidgets = new ArrayList<>();

    public SinglePageWidget leftPage = null;

    public SinglePageWidget rightPage = null;
    List<SinglePageCtor> pages;
    DocEntry entry;
    public PageHolderScreen(DocEntry entry) {
        super();
        this.entry = entry;
        this.pages = new ArrayList<>(entry.pages());
        this.maxArrowIndex = (pages.size() - 1) / 2;
    }

    @Override
    public void init() {
        super.init();
        allWidgets = new ArrayList<>();
        for(int i = 0; i < pages.size(); i++){
            SinglePageCtor page = pages.get(i);
            SinglePageWidget widget = page.create(this,  (i + 1) % 2 == 0 ? bookLeft + RIGHT_PAGE_OFFSET : bookLeft + LEFT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, 135, 180);
            allWidgets.add(widget);
        }
        initPages();
    }

    @Override
    public void initBookmarks() {
        super.initBookmarks();
        List<ResourceLocation> bookmarks = DocPlayerData.bookmarks;
        if(bookmarks.size() < 10){
            var addBookmark = addRenderableWidget(new NuggetImageButton(bookLeft + 281, bookTop + 1 + 15 * (bookmarks.size() + 1), DocAssets.BOOKMARK.width(), DocAssets.BOOKMARK.height(), DocAssets.BOOKMARK.location(), (b) -> {
                bookmarks.add(this.entry.id());
                initBookmarks();
            }).withTooltip(Component.translatable("ars_nouveau.add_bookmark").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))));
            this.bookmarkButtons.add(addBookmark);
        }
    }

    @Override
    public void transition(BaseDocScreen screen) {
        // Prevent bookmarks from transitioning to the same screen
        if(screen instanceof PageHolderScreen newPageHolder && newPageHolder.entry == this.entry){
            return;
        }
        super.transition(screen);
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
