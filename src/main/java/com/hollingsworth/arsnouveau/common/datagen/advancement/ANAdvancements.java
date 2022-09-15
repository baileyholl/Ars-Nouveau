package com.hollingsworth.arsnouveau.common.datagen.advancement;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class ANAdvancements implements Consumer<Consumer<Advancement>> {
    @Override
    public void accept(Consumer<Advancement> advancementsConsumer) {
        Advancement advancement = Advancement.Builder.advancement().display(ItemsRegistry.WORN_NOTEBOOK, translate("title"),
                Component.translatable("advancements.story.root.description"), new ResourceLocation("ars_nouveau:textures/gui/advancements/backgrounds/sourcestone.png"), FrameType.TASK,
                false, false, false).addCriterion("ars_nouveau:worn_notebook", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK)).save(advancementsConsumer, "ars_nouveau:root");



    }

    public static MutableComponent translate(String key){
        return Component.translatable("ars_nouveau.advancements." + key);
    }
}
