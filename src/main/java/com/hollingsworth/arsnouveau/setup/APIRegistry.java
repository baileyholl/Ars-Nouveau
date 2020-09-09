package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class APIRegistry {

    public static void registerApparatusRecipes(){
        registerApparatusRecipe(new EnchantingApparatusRecipe(ItemsRegistry.beltOfLevitation, ItemsRegistry.mundaneBelt, new Item[]
                {Items.GOLD_INGOT,Items.GOLD_INGOT,Items.GOLD_INGOT,Items.GOLD_INGOT,
                Items.FEATHER,Items.FEATHER,Items.FEATHER, ArsNouveauAPI.getInstance().getGlyphItem(ModConfig.EffectLaunchID)},  ArsNouveauAPI.PatchouliCategories.equipment.name()));

        registerApparatusRecipe(new EnchantingApparatusRecipe(ItemsRegistry.jarOfLight, Items.GLASS_BOTTLE, new Item[]
                {Items.GLOWSTONE, Items.GLOWSTONE, Items.GLOWSTONE, Items.GLOWSTONE, Items.REDSTONE_LAMP, Items.GLASS, Items.GLASS, Items.REDSTONE_LAMP}, ArsNouveauAPI.PatchouliCategories.equipment.name()));

        registerApparatusRecipe(new EnchantingApparatusRecipe( BlockRegistry.MANA_BLOOM_CROP.asItem(), Items.WHEAT_SEEDS, new Item[]
                {ItemsRegistry.bucketOfMana, ItemsRegistry.bucketOfMana, ItemsRegistry.bucketOfMana, ItemsRegistry.bucketOfMana},  ArsNouveauAPI.PatchouliCategories.resources.name()));

        registerApparatusRecipe(new EnchantingApparatusRecipe(ItemsRegistry.ringOfLesserDiscount, ItemsRegistry.ringOfPotential, new Item[]{
                Items.DIAMOND, Items.ENDER_PEARL, Items.DIAMOND, ItemsRegistry.arcaneBrick, ItemsRegistry.arcaneBrick, Items.DIAMOND, Items.ENDER_PEARL, Items.DIAMOND
        }, ArsNouveauAPI.PatchouliCategories.equipment.name()));

        registerApparatusRecipe(new EnchantingApparatusRecipe(ItemsRegistry.ringOfGreaterDiscount, ItemsRegistry.ringOfLesserDiscount, new Item[]{
                Items.DIAMOND, Items.BLAZE_ROD, Items.DIAMOND, ItemsRegistry.arcaneBrick, ItemsRegistry.arcaneBrick, Items.DIAMOND, Items.BLAZE_ROD, Items.DIAMOND
        }, ArsNouveauAPI.PatchouliCategories.equipment.name()));
        registerApparatusRecipe(new EnchantingApparatusRecipe(ItemsRegistry.beltOfUnstableGifts, ItemsRegistry.mundaneBelt, new Item[]{
                Items.SUGAR, Items.NETHER_WART, Items.BLAZE_POWDER, Items.GLOWSTONE_DUST, Items.FERMENTED_SPIDER_EYE, Items.REDSTONE, Items.BREWING_STAND, Items.FEATHER
        }, ArsNouveauAPI.PatchouliCategories.equipment.name()));

        registerApparatusRecipe(new EnchantingApparatusRecipe(BlockRegistry.SUMMONING_CRYSTAL.asItem(), Items.DIAMOND_BLOCK, new Item[]{
                Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, ItemsRegistry.arcaneBrick, ItemsRegistry.arcaneBrick, ItemsRegistry.arcaneBrick, ItemsRegistry.arcaneBrick
        }, ArsNouveauAPI.PatchouliCategories.automation.name()));

        registerApparatusRecipe(new EnchantingApparatusRecipe(ItemsRegistry.whelpCharm, Items.EGG, new Item[]{
                ItemsRegistry.bucketOfMana,ItemsRegistry.bucketOfMana,Items.DIAMOND, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, ItemsRegistry.noviceSpellBook,
        }, ArsNouveauAPI.PatchouliCategories.automation.name()));

        registerApparatusRecipe(new EnchantingApparatusRecipe(BlockRegistry.ARCANE_RELAY.asItem(), BlockRegistry.MANA_JAR.asItem(), new Item[]{
                ItemsRegistry.bucketOfMana,ItemsRegistry.bucketOfMana,Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT,
        }, ArsNouveauAPI.PatchouliCategories.automation.name()));


        registerApparatusRecipe(new EnchantingApparatusRecipe(ItemsRegistry.amuletOfManaBoost, ItemsRegistry.dullTrinket, new Item[]{
                Items.DIAMOND, Items.DIAMOND, Items.DIAMOND, ItemsRegistry.bucketOfMana, ItemsRegistry.bucketOfMana, ItemsRegistry.bucketOfMana, ItemsRegistry.bucketOfMana, ItemsRegistry.bucketOfMana
        }, ArsNouveauAPI.PatchouliCategories.equipment.name()));

        registerApparatusRecipe(new EnchantingApparatusRecipe(ItemsRegistry.amuletOfManaRegen, ItemsRegistry.dullTrinket, new Item[]{
                Items.DIAMOND, Items.DIAMOND, Items.GOLD_INGOT, Items.GOLD_INGOT, ItemsRegistry.bucketOfMana, ItemsRegistry.bucketOfMana, ItemsRegistry.bucketOfMana, ItemsRegistry.bucketOfMana
        }, ArsNouveauAPI.PatchouliCategories.equipment.name()));

    }

    public static void registerSpells(){
        registerSpell(ModConfig.MethodProjectileID, new MethodProjectile());
        registerSpell(ModConfig.MethodTouchID, new MethodTouch());
        registerSpell(ModConfig.MethodSelfID, new MethodSelf());
//        registerSpell(ModConfig.MethodBeamID, new MethodBeam());
        registerSpell(ModConfig.MethodRuneID, new MethodRune());
        registerSpell(ModConfig.EffectBreakID, new EffectBreak());
        registerSpell(ModConfig.EffectHarmID, new EffectHarm());
        registerSpell(ModConfig.EffectIgniteID, new EffectIgnite());
        registerSpell(ModConfig.EffectPhantomBlockID, new EffectPhantomBlock());
        registerSpell(ModConfig.EffectHealID, new EffectHeal());
        registerSpell(ModConfig.EffectGrowID, new EffectGrow());
        registerSpell(ModConfig.EffectKnockbackID, new EffectKnockback());
        registerSpell(ModConfig.EffectHasteID, new EffectHaste());
        registerSpell(ModConfig.EffectLightID, new EffectLight());
        registerSpell(ModConfig.EffectDispelID, new EffectDispel());
        registerSpell(ModConfig.EffectFreezeID, new EffectFreeze());
        registerSpell(ModConfig.EffectLaunchID, new EffectLaunch());
        registerSpell(ModConfig.EffectPullID, new EffectPull());
        registerSpell(ModConfig.EffectBlinkID, new EffectBlink());
        registerSpell(ModConfig.EffectExplosionID, new EffectExplosion());
        registerSpell(ModConfig.EffectLightningID, new EffectLightning());
        registerSpell(ModConfig.EffectSlowfallID, new EffectSlowfall());
        registerSpell(ModConfig.EffectShieldID, new EffectShield());
        registerSpell(ModConfig.EffectAquatic, new EffectAquatic());
        registerSpell(ModConfig.EffectFangsID, new EffectFangs());
        registerSpell(ModConfig.EffectSummonVexID, new EffectSummonVex());
        registerSpell(ModConfig.EffectStrength, new EffectStrength());
        registerSpell(ModConfig.AugmentAccelerateID, new AugmentAccelerate());
        registerSpell(ModConfig.AugmentSplitID, new AugmentSplit());
        registerSpell(ModConfig.AugmentAmplifyID, new AugmentAmplify());
        registerSpell(ModConfig.AugmentAOEID, new AugmentAOE());
        registerSpell(ModConfig.AugmentExtendTimeID, new AugmentExtendTime());
        registerSpell(ModConfig.AugmentPierceID, new AugmentPierce());
        registerSpell(ModConfig.AugmentDampenID, new AugmentDampen());
        registerSpell(ModConfig.AugmentExtractID, new AugmentExtract());
        registerSpell(ModConfig.AugmentFortuneID, new AugmentFortune());
        registerSpell(ModConfig.EffectEnderChestID, new EffectEnderChest());
        registerSpell(ModConfig.EffectHarvestID, new EffectHarvest());
        registerSpell(ModConfig.EffectPickupID, new EffectPickup());
        registerSpell(ModConfig.EffectInteractID, new EffectInteract());
        registerSpell(ModConfig.EffectPlaceBlockID, new EffectPlaceBlock());
        addStartingSpell(ModConfig.MethodProjectileID);
        addStartingSpell(ModConfig.MethodTouchID);
        addStartingSpell(ModConfig.MethodSelfID);
        addStartingSpell(ModConfig.EffectBreakID);
        addStartingSpell(ModConfig.EffectHarmID);
    }

    public static void registerApparatusRecipe(EnchantingApparatusRecipe recipe){
        System.out.println(recipe);
        ArsNouveauAPI.getInstance().getEnchantingApparatusRecipes().add(recipe);
    }

    public static void addStartingSpell(String spellTag){
        ArsNouveauAPI.getInstance().addStartingSpell(spellTag);
    }

    public static void registerSpell(String id, AbstractSpellPart spellPart){
        ArsNouveauAPI.getInstance().registerSpell(id, spellPart);
    }
}
