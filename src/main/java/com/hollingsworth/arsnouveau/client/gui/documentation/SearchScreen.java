package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.search.Search;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class SearchScreen extends BaseDocScreen{
    List<DocEntryButton> searchResults = new ArrayList<>();
    List<Search.Result> resultDocs = new ArrayList<>();
    public SearchScreen(String searchString){
        super();
        previousString = searchString;
    }

    @Override
    public void init() {
        super.init();
        onSearchChanged(previousString);
        searchBar.setValue(previousString);
        searchBar.mouseClicked(0, 0, 1);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        DocClientUtils.drawHeader(Component.translatable("ars_nouveau.doc.search_results"), graphics, screenLeft + LEFT_PAGE_OFFSET, screenTop + PAGE_TOP_OFFSET, ONE_PAGE_WIDTH, mouseX, mouseY, partialTicks);

        DocClientUtils.blit(graphics, DocAssets.SEARCH_SPLASH, screenLeft + LEFT_PAGE_OFFSET + DocAssets.SEARCH_SPLASH.width() / 2 - 10, screenBottom - DocAssets.SEARCH_SPLASH.height() - 30);
        DocClientUtils.drawParagraph(Component.translatable("ars_nouveau.search_desc"), graphics, screenLeft + LEFT_PAGE_OFFSET, screenTop + PAGE_TOP_OFFSET + 20, ONE_PAGE_WIDTH, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onArrowIndexChange() {
        maxArrowIndex = (resultDocs.size() - 1) / 9;
        super.onArrowIndexChange();
        for(DocEntryButton button : searchResults){
            removeWidget(button);
        }
        searchResults.clear();
        getRightPageButtons(resultDocs, arrowIndex * 9, (arrowIndex + 1) * 9);
    }


    public void getRightPageButtons(List<Search.Result> docs, int from, int to){
        if(from > docs.size()){
            return;
        }
        var slicedDocs = docs.subList(from, Math.min(to, docs.size()));
        for(int i = 0; i < Math.min(slicedDocs.size(), to); i++){
            var entry = slicedDocs.get(i);
            var button = new DocEntryButton(screenLeft + RIGHT_PAGE_OFFSET, screenTop + PAGE_TOP_OFFSET  +  (16 * i), entry.entry(), (b) -> {
                previousScreen.transition(new PageHolderScreen(entry.entry()));
            });
            addRenderableWidget(button);
            searchResults.add(button);
        }
    }

    @Override
    public void onSearchChanged(String str) {
        if (previousString != null && str.equals(previousString))
            return;
        previousString = str;

        if(str.isEmpty()){
            previousScreen.previousString = "";
            goBack();
        }else {
            resultDocs = Search.search(previousString);
            onArrowIndexChange();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        searchBar.setFocused(true);
        searchBar.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
