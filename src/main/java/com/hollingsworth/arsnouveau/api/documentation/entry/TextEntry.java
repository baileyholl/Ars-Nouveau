package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageWidget;
import com.hollingsworth.arsnouveau.api.documentation.export.DocExporter;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.nuggets.client.gui.NuggetMultilLineLabel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;

public class TextEntry extends SinglePageWidget {
    Component body;
    @Nullable Component title;
    @Nullable ItemStack renderStack;
    NuggetMultilLineLabel titleLabel;
    public TextEntry(Component body, Component title, ItemStack renderStack, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.body = body;
        this.title = title;
        this.renderStack = renderStack;
        if(title != null){
            this.titleLabel = NuggetMultilLineLabel.create(Minecraft.getInstance().font, title, 95);
        }
    }

    public static SinglePageCtor create(Component body, Component title, ItemStack renderStack){
        return (parent, x, y, width, height) -> new TextEntry(body, title, renderStack, parent, x, y, width, height);
    }

    public static SinglePageCtor create(Component body, Component title, ItemLike renderStack){
        return (parent, x, y, width, height) -> new TextEntry(body, title, renderStack.asItem().getDefaultInstance(), parent, x, y, width, height);
    }

    public static SinglePageCtor create(Component body, Component title){
        return (parent, x, y, width, height) -> new TextEntry(body, title, null, parent, x, y, width, height);
    }

    public static SinglePageCtor create(Component body){
        return (parent, x, y, width, height) -> new TextEntry(body, null, null, parent, x, y, width, height);
    }

    public static SinglePageCtor create(String body){
        return (parent, x, y, width, height) -> new TextEntry(Component.translatable(body), null, null, parent, x, y, width, height);
    }

    public static SinglePageCtor create(String body, String title){
        return (parent, x, y, width, height) -> new TextEntry(Component.translatable(body), Component.translatable(title), null, parent, x, y, width, height);
    }

    public int drawTitle(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks){
        Font font = Minecraft.getInstance().font;
        if(renderStack != null){
            DocClientUtils.blit(guiGraphics, DocAssets.HEADER_WITH_ITEM, x , y - 1);
            setTooltipIfHovered(DocClientUtils.renderItemStack(guiGraphics, x + 3, y + 2, mouseX, mouseY, renderStack));
            DocClientUtils.drawHeader(titleLabel, guiGraphics, x + 70, y - 1);
            return 24;
        }else{
            DocClientUtils.drawHeader(title, guiGraphics, x, y, width, mouseX, mouseY, partialTicks);
        }
        return 20;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        boolean hasTitle = title != null;
        int yOffset = 0;
        if(hasTitle){
           yOffset = drawTitle(guiGraphics, mouseX, mouseY, partialTick);
        }
        DocClientUtils.drawParagraph(body, guiGraphics, x, y + yOffset, width, mouseX, mouseY, partialTick);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void addExportProperties(JsonObject object) {
        if(title != null){
            object.addProperty(DocExporter.TITLE_PROPERTY, title.getString());
        }
        object.addProperty(DocExporter.DESCRIPTION_PROPERTY, body.getString());
        if(renderStack != null && !renderStack.isEmpty()) {
            object.addProperty(DocExporter.ICON_PROPERTY, BuiltInRegistries.ITEM.getKey(renderStack.getItem()).toString());
        }
    }
}
