package com.hollingsworth.arsnouveau.common.datagen.advancement;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class ANAdvancements implements Consumer<Consumer<Advancement>> {
    @Override
    public void accept(Consumer<Advancement> con) {
        Advancement root = builder(ArsNouveau.MODID).display(ItemsRegistry.WORN_NOTEBOOK, Component.translatable("ars_nouveau.advancement.title.root"),
                Component.translatable("ars_nouveau.advancement.desc.root"),
                new ResourceLocation("ars_nouveau:textures/gui/advancements/backgrounds/sourcestone.png"),
                FrameType.TASK, false, false, false).addCriterion("ars_nouveau:worn_notebook",
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK)).save(con, "ars_nouveau:root");

        Advancement novice = builder("novice_book").normalItemRequirement(ItemsRegistry.NOVICE_SPELLBOOK).parent(root).save(con);
        Advancement mages = builder("apprentice_book").normalItemRequirement(ItemsRegistry.APPRENTICE_SPELLBOOK).parent(novice).save(con);
        Advancement archmage_book = builder("archmage_book").normalItemRequirement(ItemsRegistry.ARCHMAGE_SPELLBOOK).parent(mages).save(con);
    }

    public ANAdvancementBuilder builder(String key){
        return ANAdvancementBuilder.builder(ArsNouveau.MODID, key);
    }
}
