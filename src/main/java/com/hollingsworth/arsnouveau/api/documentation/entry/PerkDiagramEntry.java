package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageWidget;
import com.hollingsworth.arsnouveau.api.documentation.export.DocExporter;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public class PerkDiagramEntry extends SinglePageWidget {
    public ItemStack item1;
    public ItemStack item2;
    public ItemStack item3;
    public ItemStack item4;
    public PerkDiagramEntry(BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }

    public static SinglePageCtor create(ItemLike item1, ItemLike item2, ItemLike item3, ItemLike item4){
        return (parent, x, y, width, height) -> {
            PerkDiagramEntry entry = new PerkDiagramEntry(parent, x, y, width, height);
            entry.item1 = item1.asItem().getDefaultInstance();
            entry.item2 = item2.asItem().getDefaultInstance();
            entry.item3 = item3.asItem().getDefaultInstance();
            entry.item4 = item4.asItem().getDefaultInstance();
            return entry;
        };
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        DocClientUtils.drawHeader(Component.translatable("ars_nouveau.slots_armor"), guiGraphics, x, y, width, mouseX, mouseY, partialTick);
        int xOffset = 10;
        DocClientUtils.blit(guiGraphics, DocAssets.THREAD_FRAME, x + xOffset, y + 19);
        int fontYOffset = 111;
        int fontXOffset = xOffset + 21;
        DocClientUtils.drawParagraph(Component.literal("Tier of armor"), guiGraphics, x + fontXOffset, y + fontYOffset, width - 2, mouseX, mouseY, partialTick);
        DocClientUtils.drawParagraph(Component.translatable("ars_nouveau.thread_tier", 1), guiGraphics, x + fontXOffset, y + fontYOffset + 10, width - 2, mouseX, mouseY, partialTick);
        DocClientUtils.drawParagraph(Component.translatable("ars_nouveau.thread_tier", 2), guiGraphics, x + fontXOffset, y + fontYOffset + 20, width - 2, mouseX, mouseY, partialTick);
        DocClientUtils.drawParagraph(Component.translatable("ars_nouveau.thread_tier", 3), guiGraphics, x + fontXOffset, y + fontYOffset + 30, width - 2, mouseX, mouseY, partialTick);

        int itemYOffset = 30;
        setTooltipIfHovered(DocClientUtils.renderItemStack(guiGraphics,x + xOffset + 2, y + itemYOffset, mouseX, mouseY, item1));

        setTooltipIfHovered(DocClientUtils.renderItemStack(guiGraphics,x + xOffset + 2, y + itemYOffset + 19, mouseX, mouseY, item2));

        setTooltipIfHovered(DocClientUtils.renderItemStack(guiGraphics,x + xOffset + 2, y + itemYOffset + 19 * 2, mouseX, mouseY, item3));

        setTooltipIfHovered(DocClientUtils.renderItemStack(guiGraphics,x + xOffset + 2, y + itemYOffset + 19 * 3, mouseX, mouseY, item4));


        int perkXOffset = 35;
        int perkYOffset = 35;
        drawRow(guiGraphics, x + perkXOffset, y + perkYOffset, item1);
        drawRow(guiGraphics, x + perkXOffset, y + perkYOffset + 19, item2);
        drawRow(guiGraphics, x + perkXOffset, y + perkYOffset + 19 * 2, item3);
        drawRow(guiGraphics, x + perkXOffset, y + perkYOffset + 19 * 3, item4);

    }

    public void drawRow(GuiGraphics graphics, int x, int y, ItemStack stack){
        var provider = PerkRegistry.getPerkProvider(stack);
        if(provider != null){
            for(int tier = 0; tier < provider.size(); tier++){
                var levelOne = provider.get(tier);
                int extraOffset = switch (tier){
                    case 0 -> 0;
                    case 1 -> 6;
                    case 2 -> 20;
                    default -> 0;
                };
                drawSlotList(graphics, x + tier * 12 +extraOffset, y, levelOne);
            }
        }
    }

    public void drawSlotList(GuiGraphics graphics, int x, int y, List<PerkSlot> perks){
        for(int i = 0; i < perks.size(); i++){
            var perk = perks.get(i);
            DocClientUtils.blit(graphics, perk.icon(), x + i * 8, y);
        }
    }

    @Override
    public void addExportProperties(JsonObject object) {
        super.addExportProperties(object);
        if(item1 != null){
            JsonArray perkData = getPerkRowExport(item1);
            JsonObject item1Data = new JsonObject();
            item1Data.addProperty(DocExporter.ITEM_PROPERTY, BuiltInRegistries.ITEM.getKey(item1.getItem()).toString());
            item1Data.add(DocExporter.PERKS_PROPERTY, perkData);
            object.add("item1", item1Data);
        }

        if(item2 != null){
            JsonArray perkData = getPerkRowExport(item2);
            JsonObject item2Data = new JsonObject();
            item2Data.addProperty(DocExporter.ITEM_PROPERTY, BuiltInRegistries.ITEM.getKey(item2.getItem()).toString());
            item2Data.add(DocExporter.PERKS_PROPERTY, perkData);
            object.add("item2", item2Data);
        }

        if(item3 != null){
            JsonArray perkData = getPerkRowExport(item3);
            JsonObject item3Data = new JsonObject();
            item3Data.addProperty(DocExporter.ITEM_PROPERTY, BuiltInRegistries.ITEM.getKey(item3.getItem()).toString());
            item3Data.add(DocExporter.PERKS_PROPERTY, perkData);
            object.add("item3", item3Data);
        }

        if(item4 != null){
            JsonArray perkData = getPerkRowExport(item4);
            JsonObject item4Data = new JsonObject();
            item4Data.addProperty(DocExporter.ITEM_PROPERTY, BuiltInRegistries.ITEM.getKey(item4.getItem()).toString());
            item4Data.add(DocExporter.PERKS_PROPERTY, perkData);
            object.add("item4", item4Data);
        }

    }

    public JsonArray getPerkRowExport(ItemStack stack){
        var provider = PerkRegistry.getPerkProvider(stack);
        JsonArray tiersData = new JsonArray();
        if(provider != null){
            for(int tier = 0; tier < provider.size(); tier++){
                JsonObject tierData = new JsonObject();
                tierData.addProperty("tier", tier);

                JsonArray perkSlots = new JsonArray();
                for(PerkSlot slot : provider.get(tier)){
                    perkSlots.add(slot.value());
                }
                tierData.add("perks", perkSlots);
                tiersData.add(tierData);
            }
        }
        return tiersData;
    }
}
