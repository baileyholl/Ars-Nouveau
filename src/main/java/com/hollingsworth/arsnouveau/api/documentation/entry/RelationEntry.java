package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageWidget;
import com.hollingsworth.arsnouveau.api.documentation.export.DocExporter;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.client.gui.documentation.DocEntryButton;
import com.hollingsworth.arsnouveau.client.gui.documentation.PageHolderScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
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
        this.relatedEntries = relatedEntries;
    }

    public static SinglePageCtor create() {
        return (parent, x, y, width, height) -> new RelationEntry(parent, x, y, width, height);
    }

    public static SinglePageCtor create(List<ResourceLocation> relatedEntries) {
        return (parent, x, y, width, height) -> new RelationEntry(relatedEntries, parent, x, y, width, height);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        DocClientUtils.drawHeader(Component.translatable("ars_nouveau.doc.related_entries"), guiGraphics, x, y, width, mouseX, mouseY, partialTick);
    }

    @Override
    public List<AbstractWidget> getExtras() {
        List<AbstractWidget> entryButtons = super.getExtras();
        int i = 0;
        for (ResourceLocation id : relatedEntries) {
            DocEntry entry = DocumentationRegistry.getEntry(id);
            var button = new DocEntryButton(x, y + 16 + 16 * i, entry, (b) -> {
                parent.transition(new PageHolderScreen(entry));
            });
            entryButtons.add(button);
            i++;
        }
        return entryButtons;
    }

    public static class RelationBuilder implements SinglePageCtor {

        public List<ResourceLocation> entries = new ArrayList<>();

        public RelationBuilder withEntry(DocEntry entry) {
            entries.add(entry.id());
            return this;
        }

        public RelationBuilder withEntries(List<DocEntry> entries) {
            this.entries.addAll(entries.stream().map(DocEntry::id).toList());
            return this;
        }

        @Override
        public SinglePageWidget create(BaseDocScreen parent, int x, int y, int width, int height) {
            return new RelationEntry(entries, parent, x, y, width, height);
        }
    }

    @Override
    public void addExportProperties(JsonObject object) {
        super.addExportProperties(object);
        JsonArray array = new JsonArray();
        for (ResourceLocation id : relatedEntries) {
            array.add(id.toString());
        }
        object.add(DocExporter.RELATED_PROPERTY, array);
    }
}
