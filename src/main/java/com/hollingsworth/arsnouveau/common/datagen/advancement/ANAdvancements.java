package com.hollingsworth.arsnouveau.common.datagen.advancement;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.advancement.ANCriteriaTriggers;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class ANAdvancements implements Consumer<Consumer<Advancement>> {
    @Override
    public void accept(Consumer<Advancement> con) {
        Advancement root = builder(ArsNouveau.MODID).display(ItemsRegistry.WORN_NOTEBOOK, Component.translatable("ars_nouveau.advancement.title.root"),
                Component.translatable("ars_nouveau.advancement.desc.root"),
                new ResourceLocation("ars_nouveau:textures/gui/advancements/backgrounds/sourcestone.png"),
                FrameType.TASK, false, false, false).addCriterion("ars_nouveau:worn_notebook",
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK)).save(con, "ars_nouveau:root");
        Advancement poofMob = builder("poof_mob").display(Items.GOLD_NUGGET, FrameType.TASK).addCriterion(new PlayerTrigger.TriggerInstance(ANCriteriaTriggers.POOF_MOB.getId(), EntityPredicate.Composite.ANY)).parent(root).save(con);
        builder("wixie_charm").normalItemRequirement(ItemsRegistry.WIXIE_CHARM).parent(poofMob).save(con);
        builder("whirlisprig_charm").normalItemRequirement(ItemsRegistry.WHIRLISPRIG_CHARM).parent(poofMob).save(con);


        Advancement starbyCharm = builder("starby_charm").normalItemRequirement(ItemsRegistry.STARBUNCLE_CHARM).parent(poofMob).save(con);
        builder("starby_glasses").normalItemRequirement(ItemsRegistry.STARBUNCLE_SHADES).parent(starbyCharm).save(con);
        builder("starby_witch_hat").normalItemRequirement(ItemsRegistry.WIXIE_HAT).parent(starbyCharm).save(con);
        Advancement novice = builder("novice_book").normalItemRequirement(ItemsRegistry.NOVICE_SPELLBOOK).parent(root).save(con);
        Advancement mages = builder("apprentice_book").normalItemRequirement(ItemsRegistry.APPRENTICE_SPELLBOOK).parent(novice).save(con);
        Advancement archmage_book = builder("archmage_book").normalItemRequirement(ItemsRegistry.ARCHMAGE_SPELLBOOK).parent(mages).save(con);

        builder("eat_bombegranate").display(BlockRegistry.BOMBEGRANTE_POD, FrameType.TASK, true).addCriterion(ConsumeItemTrigger.TriggerInstance.usedItem(BlockRegistry.BOMBEGRANTE_POD)).parent(root).save(con);

        Advancement rituals = builder("rituals").normalItemRequirement(BlockRegistry.RITUAL_BLOCK).parent(root).save(con);
        builder("amethyst_golem").normalItemRequirement(ItemsRegistry.AMETHYST_GOLEM_CHARM).parent(rituals).save(con);
        builder("familiar").display(ArsNouveauAPI.getInstance().getRitualItemMap().get(new ResourceLocation(ArsNouveau.MODID, RitualLib.BINDING)), FrameType.GOAL)
                .addCriterion(new PlayerTrigger.TriggerInstance(ANCriteriaTriggers.FAMILIAR.getId(), EntityPredicate.Composite.ANY)).parent(rituals).save(con);
    }

    public ANAdvancementBuilder builder(String key){
        return ANAdvancementBuilder.builder(ArsNouveau.MODID, key);
    }
}
