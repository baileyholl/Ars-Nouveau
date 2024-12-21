package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.client.gui.documentation.DocEntryButton;
import com.hollingsworth.arsnouveau.client.gui.documentation.PageHolderScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class RelationEntry extends SinglePageWidget {
    public List<ResourceLocation> relatedEntries = new ArrayList<>();

    public RelationEntry(BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }

    public RelationEntry(List<ResourceLocation> relatedEntries, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }

    public static SinglePageCtor create(){
        return (parent, x, y, width, height) -> new RelationEntry(parent, x, y, width, height);
    }

    public static SinglePageCtor create(List<ResourceLocation> relatedEntries){
        return (parent, x, y, width, height) -> new RelationEntry(relatedEntries, parent, x, y, width, height);
    }

    @Override
    public List<AbstractWidget> getExtras() {
        List<AbstractWidget> entryButtons = super.getExtras();
        int i = 0;
        for(ResourceLocation id : relatedEntries){
            DocEntry entry = DocumentationRegistry.getEntry(id);
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

    public static class RelationBuilder implements SinglePageCtor{

        public List<ResourceLocation> entries = new ArrayList<>();

        public RelationBuilder withEntry(DocEntry entry){
            entries.add(entry.id());
            return this;
        }

        public RelationBuilder withEntries(List<DocEntry> entries){
            this.entries.addAll(entries.stream().map(DocEntry::id).toList());
            return this;
        }

        @Override
        public SinglePageWidget create(BaseDocScreen parent, int x, int y, int width, int height) {
            return new RelationEntry(entries, parent, x, y, width, height);
        }
    }
    public static class BuilderImpl{
        public List<ResourceLocation> entries = new ArrayList<>();

        public BuilderImpl withEntry(DocEntry entry){
            entries.add(entry.id());
            return this;
        }

        public BuilderImpl withEntries(List<DocEntry> entries){
            this.entries.addAll(entries.stream().map(DocEntry::id).toList());
            return this;
        }

        public SinglePageCtor build(){
            return (parent, x, y, width, height) -> new RelationEntry(entries, parent, x, y, width, height);
        }
    }
}
