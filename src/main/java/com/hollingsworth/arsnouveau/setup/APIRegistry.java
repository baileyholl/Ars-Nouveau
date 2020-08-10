package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.items.ItemsRegistry;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class APIRegistry {

    public static void initApi(){
        registerSpells();
    }

    public static void registerApparatusRecipes(){
        registerApparatusRecipe(new EnchantingApparatusRecipe(ItemsRegistry.beltOfLevitation, ItemsRegistry.mundaneBelt, new Item[]
                {Items.GOLD_INGOT,Items.GOLD_INGOT,Items.GOLD_INGOT,Items.GOLD_INGOT,
                Items.FEATHER,Items.FEATHER,Items.FEATHER, ArsNouveauAPI.getInstance().getGlyphItem(ModConfig.EffectLaunchID)},  ArsNouveauAPI.PatchouliCategories.equipment.name()));

        registerApparatusRecipe(new EnchantingApparatusRecipe(ItemsRegistry.jarOfLight, Items.GLASS_BOTTLE, new Item[]
                {Items.GLOWSTONE, Items.GLOWSTONE, Items.GLOWSTONE, Items.GLOWSTONE, Items.REDSTONE_LAMP, Items.GLASS, Items.GLASS, Items.REDSTONE_LAMP}, ArsNouveauAPI.PatchouliCategories.equipment.name()));

        registerApparatusRecipe(new EnchantingApparatusRecipe(ItemsRegistry.manaBloom, Items.WHEAT_SEEDS, new Item[]
                {ItemsRegistry.bucketOfMana, ItemsRegistry.bucketOfMana, ItemsRegistry.bucketOfMana, ItemsRegistry.bucketOfMana},  ArsNouveauAPI.PatchouliCategories.resources.name()));


    }

    public static void registerSpells(){
        registerSpell(ModConfig.MethodProjectileID, new MethodProjectile());
        registerSpell(ModConfig.MethodTouchID, new MethodTouch());
        registerSpell(ModConfig.MethodSelfID, new MethodSelf());

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
