package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.api.documentation.DocEntry;

import java.util.ArrayList;
import java.util.List;

public class EntriesScreen extends BaseDocScreen{
    List<DocEntry> entries;
    List<DocEntryButton> buttons = new ArrayList<>();

    public EntriesScreen(List<DocEntry> entries) {
        super();
        this.entries = new ArrayList<>(entries);
        for(int i = 0; i < 32; i++){
            this.entries.add(this.entries.get(0));
        }
        this.maxArrowIndex = (this.entries.size() - 1) / 18;
    }

    @Override
    public void init() {
        super.init();
        initButtons();
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
        List<DocEntry> sliced = entries.subList(arrowIndex * 18, Math.min((arrowIndex + 1) * 18, entries.size()));
        for(int i = 0; i < sliced.size(); i++){
            DocEntry entry = sliced.get(i);
            var button = new DocEntryButton(bookLeft + 18 + (i > 8 ? 135 : 0), bookTop + 24 + 16 * (i > 8 ? i - 9 : i), entry, (b) -> {
                transition(new PageHolderScreen(entry.pages()));
            });
            addRenderableWidget(button);
            buttons.add(button);
        }
    }
}
