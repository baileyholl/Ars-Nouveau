package com.hollingsworth.arsnouveau.common.datagen.advancement;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.common.advancement.ANCriteriaTriggers;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class ANAdvancements implements ForgeAdvancementProvider.AdvancementGenerator {
    Consumer<Advancement> advCon;

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> con, ExistingFileHelper existingFileHelper) {
        this.advCon = con;
        Advancement root = builder(ArsNouveau.MODID).display(ItemsRegistry.WORN_NOTEBOOK, Component.translatable("ars_nouveau.advancement.title.root"),
                Component.translatable("ars_nouveau.advancement.desc.root"),
                new ResourceLocation("ars_nouveau:textures/gui/advancements/backgrounds/sourcestone.png"),
                FrameType.TASK, false, false, false).addCriterion("ars_nouveau:worn_notebook",
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK)).save(con, "ars_nouveau:root");
        Advancement poofMob = builder("poof_mob").display(Items.GOLD_NUGGET, FrameType.TASK).addCriterion(new PlayerTrigger.TriggerInstance(ANCriteriaTriggers.POOF_MOB.getId(), ContextAwarePredicate.ANY)).parent(root).save(con);
        saveBasicItem(ItemsRegistry.WIXIE_CHARM, poofMob);
        saveBasicItem(ItemsRegistry.WHIRLISPRIG_CHARM, poofMob);
        saveBasicItem(ItemsRegistry.DRYGMY_CHARM, poofMob);
        Advancement starbyCharm = builder("starby_charm").normalItemRequirement(ItemsRegistry.STARBUNCLE_CHARM).parent(poofMob).save(con);
        saveBasicItem(ItemsRegistry.STARBUNCLE_SHADES, starbyCharm);
        saveBasicItem(ItemsRegistry.WIXIE_HAT, starbyCharm);
        Advancement novice = saveBasicItem(ItemsRegistry.NOVICE_SPELLBOOK, root);
        Advancement mages = saveBasicItem(ItemsRegistry.APPRENTICE_SPELLBOOK, novice);
        var tribue = saveBasicItem(ItemsRegistry.WILDEN_TRIBUTE, mages);
        builder("wilden_explosion").display(ItemsRegistry.WILDEN_TRIBUTE, FrameType.CHALLENGE, true).addCriterion(new PlayerTrigger.TriggerInstance(ANCriteriaTriggers.CHIMERA_EXPLOSION.getId(),ContextAwarePredicate.ANY)).parent(tribue).save(con);
        saveBasicItem(ItemsRegistry.ARCHMAGE_SPELLBOOK, tribue);
        saveBasicItem(ItemsRegistry.SUMMONING_FOCUS, tribue);
        saveBasicItem(ItemsRegistry.SHAPERS_FOCUS, novice);


        builder("eat_bombegranate").display(BlockRegistry.BOMBEGRANTE_POD, FrameType.TASK, true).addCriterion(ConsumeItemTrigger.TriggerInstance.usedItem(BlockRegistry.BOMBEGRANTE_POD)).parent(root).save(con);

        Advancement rituals = saveBasicItem(BlockRegistry.RITUAL_BLOCK, root);
        saveBasicItem(ItemsRegistry.AMETHYST_GOLEM_CHARM, rituals);
        builder("familiar").display(RitualRegistry.getRitualItemMap().get(new ResourceLocation(ArsNouveau.MODID, RitualLib.BINDING)), FrameType.GOAL)
                .addCriterion(new PlayerTrigger.TriggerInstance(ANCriteriaTriggers.FAMILIAR.getId(),ContextAwarePredicate.ANY)).parent(rituals).save(con);
        var jars = saveBasicItem(BlockRegistry.MOB_JAR, rituals);
        builder("shrunk_starbuncle").display(ItemsRegistry.STARBUNCLE_CHARM, FrameType.CHALLENGE, true).addCriterion(new PlayerTrigger.TriggerInstance(ANCriteriaTriggers.SHRUNK_STARBY.getId(),ContextAwarePredicate.ANY)).parent(jars).save(con);
        builder("catch_lightning").display(Items.LIGHTNING_ROD, FrameType.CHALLENGE, true).addCriterion(new PlayerTrigger.TriggerInstance(ANCriteriaTriggers.CAUGHT_LIGHTNING.getId(),ContextAwarePredicate.ANY)).parent(jars).save(con);
        builder("time_in_a_bottle").display(Items.CLOCK, FrameType.CHALLENGE, true).addCriterion(new PlayerTrigger.TriggerInstance(ANCriteriaTriggers.TIME_IN_BOTTLE.getId(),ContextAwarePredicate.ANY)).parent(jars).save(con);

        var chamber = saveBasicItem(BlockRegistry.IMBUEMENT_BLOCK, root);
        var jar = saveBasicItem(BlockRegistry.SOURCE_JAR, chamber);
        var apparatus = saveBasicItem(BlockRegistry.ENCHANTING_APP_BLOCK, chamber);
        saveBasicItem(BlockRegistry.SCRYERS_OCULUS, apparatus);
        var potionJar = saveBasicItem(BlockRegistry.POTION_JAR, apparatus);
        saveBasicItem(BlockRegistry.POTION_MELDER, potionJar);
        saveBasicItem(BlockRegistry.POTION_DIFFUSER, potionJar);
        saveBasicItem(ItemsRegistry.POTION_FLASK, potionJar);
        var turret = saveBasicItem(BlockRegistry.BASIC_SPELL_TURRET, apparatus);
        var prism = saveBasicItem(BlockRegistry.SPELL_PRISM, turret);
        builder("prismatic").display(BlockRegistry.SPELL_PRISM, FrameType.CHALLENGE, true).addCriterion(new PlayerTrigger.TriggerInstance(ANCriteriaTriggers.PRISMATIC.getId(),ContextAwarePredicate.ANY)).parent(prism).save(con);
        var magebloom = saveBasicItem(BlockRegistry.MAGE_BLOOM_CROP, apparatus);
        var warpScroll = saveBasicItem(ItemsRegistry.WARP_SCROLL, magebloom);
        builder("create_portal").display(BlockRegistry.CREATIVE_SOURCE_JAR, FrameType.CHALLENGE, false).addCriterion(new PlayerTrigger.TriggerInstance(ANCriteriaTriggers.CREATE_PORTAL.getId(),ContextAwarePredicate.ANY)).parent(warpScroll).save(con);
        var alteration = saveBasicItem(BlockRegistry.ALTERATION_TABLE, magebloom);
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
