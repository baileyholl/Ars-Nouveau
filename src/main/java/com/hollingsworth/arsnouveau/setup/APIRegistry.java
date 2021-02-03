package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.ReactiveEnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.SpellWriteRecipe;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodRune;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class APIRegistry {

    public static void registerApparatusRecipes() {
        registerApparatusRecipe(new ReactiveEnchantmentRecipe(new ItemStack[]{new ItemStack(ItemsRegistry.spellParchment),
                new ItemStack( ArsNouveauAPI.getInstance().getGlyphItem(ModConfig.AugmentAmplifyID)),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(ModConfig.AugmentAmplifyID)),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(ModConfig.AugmentAmplifyID))}, 3000));

        registerApparatusRecipe(new EnchantmentRecipe(new ItemStack[]{
                new ItemStack(Items.BLAZE_POWDER),
                new ItemStack(Items.BLAZE_POWDER),
                new ItemStack(Items.BLAZE_POWDER),
                new ItemStack(Items.BLAZE_POWDER),
                new ItemStack(Items.GOLD_BLOCK),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(ModConfig.AugmentExtendTimeID)),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(ModConfig.AugmentAOEID)),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(ModConfig.AugmentDampenID))
        }, EnchantmentRegistry.REACTIVE_ENCHANTMENT, 2, 6000));

        registerApparatusRecipe(new EnchantmentRecipe(new ItemStack[]{
                ItemsRegistry.mythicalClay.getStack(),
                ItemsRegistry.mythicalClay.getStack(),
                ItemsRegistry.mythicalClay.getStack(),
                ItemsRegistry.mythicalClay.getStack(),
                new ItemStack(Items.ENDER_PEARL),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(ModConfig.AugmentPierceID)),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(ModConfig.AugmentExtractID)),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(ModConfig.AugmentFortuneID))
        }, EnchantmentRegistry.REACTIVE_ENCHANTMENT, 3, 9000));

        registerApparatusRecipe(new SpellWriteRecipe());
    }

    public static void registerApparatusRecipe(IEnchantingRecipe recipe){
        ArsNouveauAPI.getInstance().getEnchantingApparatusRecipes().add(recipe);
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
        registerSpell(ModConfig.EffectEnderChestID, new EffectEnderChest());
        registerSpell(ModConfig.EffectHarvestID, new EffectHarvest());
        registerSpell(ModConfig.EffectFellID, new EffectFell());
        registerSpell(ModConfig.EffectPickupID, new EffectPickup());
        registerSpell(ModConfig.EffectInteractID, new EffectInteract());
        registerSpell(ModConfig.EffectPlaceBlockID, new EffectPlaceBlock());
        registerSpell(ModConfig.MethodRuneID, new MethodRune());
        registerSpell(ModConfig.EffectSnareID, new EffectSnare());
        registerSpell(ModConfig.EffectSmeltID, new EffectSmelt());
        registerSpell(ModConfig.EffectLeapID, new EffectLeap());
        registerSpell(ModConfig.EffectDelayID, new EffectDelay());
        registerSpell(ModConfig.EffectRedstoneID, new EffectRedstone());
        registerSpell(ModConfig.EffectIntangibleID, new EffectIntangible());
        registerSpell(ModConfig.EffectInvisibilityID, new EffectInvisibility());
        registerSpell(ModConfig.AugmentDurationDown, new AugmentDurationDown());
        registerSpell(ModConfig.EffectWitherID, new EffectWither());
        registerSpell(ModConfig.EffectExchangeID, new EffectExchange());
        registerSpell(ModConfig.EffectCraftID, new EffectCraft());
        registerSpell(ModConfig.EffectFlareID, new EffectFlare());
        registerSpell(ModConfig.EffectColdSnapID, new EffectColdSnap());
        registerSpell(ModConfig.EffectConjureWaterID, new EffectConjureWater());
        registerSpell(ModConfig.EffectGravityID, new EffectGravity());
        registerSpell(ModConfig.EffectCutID, new EffectCut());
        registerSpell(ModConfig.EffectCrushID, new EffectCrush());
        registerStartingSpells();
    }

    public static void registerStartingSpells(){
        addStartingSpell(ModConfig.MethodProjectileID);
        addStartingSpell(ModConfig.MethodTouchID);
        addStartingSpell(ModConfig.MethodSelfID);
        addStartingSpell(ModConfig.EffectBreakID);
        addStartingSpell(ModConfig.EffectHarmID);
    }

    public static void addStartingSpell(String spellTag){
        ArsNouveauAPI.getInstance().addStartingSpell(spellTag);
    }

    public static void registerSpell(String id, AbstractSpellPart spellPart){
        ArsNouveauAPI.getInstance().registerSpell(id, spellPart);
    }

    private APIRegistry(){}
}
