package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.client.gui.documentation.DocEntryButton;
import com.hollingsworth.arsnouveau.client.gui.documentation.PageHolderScreen;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.ArrayList;
import java.util.List;

public class RelationEntry extends SinglePageWidget {
    public List<DocEntry> relatedEntries = new ArrayList<>();

    public RelationEntry(BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }

    public void withEntry(DocEntry entry){
        relatedEntries.add(entry);
    }

    public static SinglePageCtor create(){
        return (parent, x, y, width, height) -> new RelationEntry(parent, x, y, width, height);
    }

    @Override
    public List<AbstractWidget> getExtras() {
        List<AbstractWidget> entryButtons = super.getExtras();
        int i = 0;
        for(DocEntry entry : relatedEntries){
            var button = new DocEntryButton(x+ 5, y + 16 * i, entry, (b) -> {
                parent.transition(new PageHolderScreen(entry.pages()));
            });
            entryButtons.add(button);
            i++;
        }
        return entryButtons;
    }

    public static BuilderImpl builder(){
        return new BuilderImpl();
    }

    public static class BuilderImpl{
        public List<DocEntry> entries = new ArrayList<>();

        public BuilderImpl withEntry(DocEntry entry){
            entries.add(entry);
            return this;
        }

        public BuilderImpl withEntries(List<DocEntry> entries){
            this.entries.addAll(entries);
            return this;
        }

        public SinglePageCtor build(){
            return (parent, x, y, width, height) -> {
                RelationEntry entry = new RelationEntry(parent, x, y, width, height);
                entry.relatedEntries = entries;
                return entry;
            };
        }
    }
}
