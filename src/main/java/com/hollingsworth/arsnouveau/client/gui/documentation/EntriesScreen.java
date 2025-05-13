package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class EntriesScreen extends BaseDocScreen{
    List<DocEntry> entries;
    List<DocEntryButton> buttons = new ArrayList<>();
    DocCategory category;
    public EntriesScreen(DocCategory category) {
        super();
        this.category = category;
        var entries = new ArrayList<>(DocumentationRegistry.getEntries(category));
        entries.sort(category.entryComparator());
        this.entries = new ArrayList<>(entries);
        if(this.entries.size() > 17){
            maxArrowIndex = 1 + (this.entries.size() - 17) / 18;
        }
    }

    @Override
    public void init() {
        super.init();
        initButtons();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if(arrowIndex == 0) {
            DocClientUtils.drawHeader(category.getTitle(), graphics, screenLeft + LEFT_PAGE_OFFSET, screenTop + PAGE_TOP_OFFSET, ONE_PAGE_WIDTH, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void onArrowIndexChange() {
        super.onArrowIndexChange();
        initButtons();
    }

    public void initButtons(){
        for(DocEntryButton button : buttons){
            removeWidget(button);
        }
        buttons.clear();
        int offset = 17;
        if(arrowIndex == 0){
            getLeftPageButtons(0, 8);
            getRightPageButtons(8, offset);
        }else{
            int offsetIndex = arrowIndex * 18 - 1;
            getLeftPageButtons(offsetIndex, offsetIndex + 9);
            getRightPageButtons(offsetIndex + 9, offsetIndex + 18);
        }
    }

    public void getLeftPageButtons(int from, int to){
        List<DocEntry> sliced = entries.subList(from, Math.min(to, entries.size()));
        boolean offset = to - from == 8;
        for(int i = 0; i < sliced.size(); i++){
            DocEntry entry = sliced.get(i);
            var button = new DocEntryButton(screenLeft + LEFT_PAGE_OFFSET, screenTop + PAGE_TOP_OFFSET + 3  +  (16 * i) + (offset ? 16 : 0), entry, (b) -> {
                transition(new PageHolderScreen(entry));
            });
            addRenderableWidget(button);
            buttons.add(button);
        }
    }

    public void getRightPageButtons(int from, int to){
        if(from > entries.size()){
            return;
        }
        List<DocEntry> sliced = entries.subList(from, Math.min(to, entries.size()));
        for(int i = 0; i < sliced.size(); i++){
            DocEntry entry = sliced.get(i);
            var button = new DocEntryButton(screenLeft + RIGHT_PAGE_OFFSET, screenTop + PAGE_TOP_OFFSET + 3 + 16 * i, entry, (b) -> {
                transition(new PageHolderScreen(entry));
            });
            addRenderableWidget(button);
            buttons.add(button);
        }
    }
}
