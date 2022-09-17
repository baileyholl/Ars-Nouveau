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
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class ANAdvancements implements Consumer<Consumer<Advancement>> {
    Consumer<Advancement> advCon;
    @Override
    public void accept(Consumer<Advancement> con) {
        this.advCon = con;
        Advancement root = builder(ArsNouveau.MODID).display(ItemsRegistry.WORN_NOTEBOOK, Component.translatable("ars_nouveau.advancement.title.root"),
                Component.translatable("ars_nouveau.advancement.desc.root"),
                new ResourceLocation("ars_nouveau:textures/gui/advancements/backgrounds/sourcestone.png"),
                FrameType.TASK, false, false, false).addCriterion("ars_nouveau:worn_notebook",
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK)).save(con, "ars_nouveau:root");
        Advancement poofMob = builder("poof_mob").display(Items.GOLD_NUGGET, FrameType.TASK).addCriterion(new PlayerTrigger.TriggerInstance(ANCriteriaTriggers.POOF_MOB.getId(), EntityPredicate.Composite.ANY)).parent(root).save(con);
        saveBasicItem(ItemsRegistry.WIXIE_CHARM, poofMob);
        saveBasicItem(ItemsRegistry.WHIRLISPRIG_CHARM, poofMob);

        Advancement starbyCharm = builder("starby_charm").normalItemRequirement(ItemsRegistry.STARBUNCLE_CHARM).parent(poofMob).save(con);
        saveBasicItem(ItemsRegistry.STARBUNCLE_SHADES, starbyCharm);
        saveBasicItem(ItemsRegistry.WIXIE_HAT, starbyCharm);
        Advancement novice = saveBasicItem(ItemsRegistry.NOVICE_SPELLBOOK, root);
        Advancement mages = saveBasicItem(ItemsRegistry.APPRENTICE_SPELLBOOK, novice);
        var tribue = saveBasicItem(ItemsRegistry.WILDEN_TRIBUTE, mages);
        saveBasicItem(ItemsRegistry.ARCHMAGE_SPELLBOOK, tribue);
        saveBasicItem(ItemsRegistry.SUMMONING_FOCUS, tribue);
        saveBasicItem(ItemsRegistry.SHAPERS_FOCUS, novice);


        builder("eat_bombegranate").display(BlockRegistry.BOMBEGRANTE_POD, FrameType.TASK, true).addCriterion(ConsumeItemTrigger.TriggerInstance.usedItem(BlockRegistry.BOMBEGRANTE_POD)).parent(root).save(con);

        Advancement rituals = saveBasicItem(BlockRegistry.RITUAL_BLOCK, root);
        saveBasicItem(ItemsRegistry.AMETHYST_GOLEM_CHARM, rituals);
        builder("familiar").display(ArsNouveauAPI.getInstance().getRitualItemMap().get(new ResourceLocation(ArsNouveau.MODID, RitualLib.BINDING)), FrameType.GOAL)
                .addCriterion(new PlayerTrigger.TriggerInstance(ANCriteriaTriggers.FAMILIAR.getId(), EntityPredicate.Composite.ANY)).parent(rituals).save(con);
        var chamber = saveBasicItem(BlockRegistry.IMBUEMENT_BLOCK, root);
        var apparatus = saveBasicItem(BlockRegistry.ENCHANTING_APP_BLOCK, chamber);
        saveBasicItem(BlockRegistry.SCRYERS_OCULUS, apparatus);
        var potionJar = saveBasicItem(BlockRegistry.POTION_JAR, apparatus);
        saveBasicItem(BlockRegistry.POTION_MELDER, potionJar);
        saveBasicItem(BlockRegistry.POTION_DIFFUSER, potionJar);
        saveBasicItem(ItemsRegistry.POTION_FLASK, potionJar);
        var turret = saveBasicItem(BlockRegistry.BASIC_SPELL_TURRET, apparatus);
        saveBasicItem(BlockRegistry.SPELL_PRISM, turret);
        var magebloom = saveBasicItem(BlockRegistry.MAGE_BLOOM_CROP, apparatus);
        var alteration = saveBasicItem(BlockRegistry.ALTERATION_TABLE, magebloom);
        saveBasicItem(ItemsRegistry.BLANK_THREAD, alteration);
    }

    public ANAdvancementBuilder buildBasicItem(ItemLike item, Advancement parent){
        return builder(ForgeRegistries.ITEMS.getKey(item.asItem()).getPath()).normalItemRequirement(item).parent(parent);
    }

    public Advancement saveBasicItem(ItemLike item, Advancement parent){
        return buildBasicItem(item, parent).save(advCon);
    }

    public ANAdvancementBuilder builder(String key){
        return ANAdvancementBuilder.builder(ArsNouveau.MODID, key);
    }
}
