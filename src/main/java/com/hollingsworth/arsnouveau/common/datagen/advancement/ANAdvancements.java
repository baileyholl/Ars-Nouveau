package com.hollingsworth.arsnouveau.common.datagen.advancement;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.common.advancement.ANCriteriaTriggers;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public class ANAdvancements implements AdvancementProvider.AdvancementGenerator {
    Consumer<AdvancementHolder> advCon;

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> con, ExistingFileHelper existingFileHelper) {
        this.advCon = con;
        AdvancementHolder root = builder(ArsNouveau.MODID).display(ItemsRegistry.WORN_NOTEBOOK, Component.translatable("ars_nouveau.advancement.title.root"),
                Component.translatable("ars_nouveau.advancement.desc.root"),
                ResourceLocation.parse("ars_nouveau:textures/gui/advancements/backgrounds/sourcestone.png"),
                AdvancementType.TASK, false, false, false).addCriterion("ars_nouveau:worn_notebook",
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK)).save(con, ArsNouveau.prefix("root"));
        AdvancementHolder poofMob = builder("poof_mob").display(Items.GOLD_NUGGET, AdvancementType.TASK).addCriterion(ANCriteriaTriggers.createCriterion(ANCriteriaTriggers.POOF_MOB)).parent(root).save(con);
        saveBasicItem(ItemsRegistry.WIXIE_CHARM, poofMob);
        saveBasicItem(ItemsRegistry.WHIRLISPRIG_CHARM, poofMob);
        saveBasicItem(ItemsRegistry.DRYGMY_CHARM, poofMob);
        AdvancementHolder starbyCharm = builder("starby_charm").normalItemRequirement(ItemsRegistry.STARBUNCLE_CHARM).parent(poofMob).save(con);
        saveBasicItem(ItemsRegistry.STARBUNCLE_SHADES, starbyCharm);
        saveBasicItem(ItemsRegistry.WIXIE_HAT, starbyCharm);
        AdvancementHolder novice = saveBasicItem(ItemsRegistry.NOVICE_SPELLBOOK, root);
        AdvancementHolder mages = saveBasicItem(ItemsRegistry.APPRENTICE_SPELLBOOK, novice);
        AdvancementHolder tribute = saveBasicItem(ItemsRegistry.WILDEN_TRIBUTE, mages);
        builder("wilden_explosion").display(ItemsRegistry.WILDEN_TRIBUTE, AdvancementType.CHALLENGE, true).addCriterion(ANCriteriaTriggers.createCriterion(ANCriteriaTriggers.CHIMERA_EXPLOSION)).parent(tribute).save(con);
        saveBasicItem(ItemsRegistry.ARCHMAGE_SPELLBOOK, tribute);
        saveBasicItem(ItemsRegistry.SUMMONING_FOCUS, tribute);
        saveBasicItem(ItemsRegistry.SHAPERS_FOCUS, novice);


        builder("eat_bombegranate").display(BlockRegistry.BOMBEGRANTE_POD, AdvancementType.TASK, true).addCriterion(ConsumeItemTrigger.TriggerInstance.usedItem(BlockRegistry.BOMBEGRANTE_POD)).parent(root).save(con);

        AdvancementHolder rituals = saveBasicItem(BlockRegistry.RITUAL_BLOCK, root);
        saveBasicItem(ItemsRegistry.AMETHYST_GOLEM_CHARM, rituals);
        builder("familiar").display(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix(RitualLib.BINDING)), AdvancementType.GOAL)
                .addCriterion(ANCriteriaTriggers.createCriterion(ANCriteriaTriggers.FAMILIAR)).parent(rituals).save(con);
        var jars = saveBasicItem(BlockRegistry.MOB_JAR, rituals);
        builder("shrunk_starbuncle").display(ItemsRegistry.STARBUNCLE_CHARM, AdvancementType.CHALLENGE, true).addCriterion(ANCriteriaTriggers.createCriterion(ANCriteriaTriggers.SHRUNK_STARBY)).parent(jars).save(con);
        builder("catch_lightning").display(Items.LIGHTNING_ROD, AdvancementType.CHALLENGE, true).addCriterion(ANCriteriaTriggers.createCriterion(ANCriteriaTriggers.CAUGHT_LIGHTNING)).parent(jars).save(con);
        builder("time_in_a_bottle").display(Items.CLOCK, AdvancementType.CHALLENGE, true).addCriterion(ANCriteriaTriggers.createCriterion(ANCriteriaTriggers.TIME_IN_BOTTLE)).parent(jars).save(con);

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
        builder("prismatic").display(BlockRegistry.SPELL_PRISM, AdvancementType.CHALLENGE, true).addCriterion(ANCriteriaTriggers.createCriterion(ANCriteriaTriggers.PRISMATIC)).parent(prism).save(con);
        var magebloom = saveBasicItem(BlockRegistry.MAGE_BLOOM_CROP, apparatus);
        var warpScroll = saveBasicItem(ItemsRegistry.WARP_SCROLL, magebloom);
        builder("create_portal").display(BlockRegistry.CREATIVE_SOURCE_JAR, AdvancementType.CHALLENGE, false).addCriterion(ANCriteriaTriggers.createCriterion(ANCriteriaTriggers.CREATE_PORTAL)).parent(warpScroll).save(con);
        var alteration = saveBasicItem(BlockRegistry.ALTERATION_TABLE, magebloom);

        builder("ritual_gravity").display(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix(RitualLib.GRAVITY)), AdvancementType.GOAL).addCriterion("gravity_effect", EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.Builder.effects().and(ModPotions.GRAVITY_EFFECT))).parent(rituals).save(con);
    }

    public ANAdvancementBuilder buildBasicItem(ItemLike item, AdvancementHolder parent){
        return builder(BuiltInRegistries.ITEM.getKey(item.asItem()).getPath()).normalItemRequirement(item).parent(parent.id());
    }

    public AdvancementHolder saveBasicItem(ItemLike item, AdvancementHolder parent){
        return buildBasicItem(item, parent).save(advCon);
    }

    public ANAdvancementBuilder builder(String key){
        return ANAdvancementBuilder.builder(ArsNouveau.MODID, key);
    }
}
