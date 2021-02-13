package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.GlyphLib;
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
                new ItemStack( ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentAmplifyID)),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentAmplifyID)),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentAmplifyID))}, 3000));

        registerApparatusRecipe(new EnchantmentRecipe(new ItemStack[]{
                new ItemStack(Items.BLAZE_POWDER),
                new ItemStack(Items.BLAZE_POWDER),
                new ItemStack(Items.BLAZE_POWDER),
                new ItemStack(Items.BLAZE_POWDER),
                new ItemStack(Items.GOLD_BLOCK),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentExtendTimeID)),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentAOEID)),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentDampenID))
        }, EnchantmentRegistry.REACTIVE_ENCHANTMENT, 2, 6000));

        registerApparatusRecipe(new EnchantmentRecipe(new ItemStack[]{
                ItemsRegistry.mythicalClay.getStack(),
                ItemsRegistry.mythicalClay.getStack(),
                ItemsRegistry.mythicalClay.getStack(),
                ItemsRegistry.mythicalClay.getStack(),
                new ItemStack(Items.ENDER_PEARL),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentPierceID)),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentExtractID)),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentFortuneID))
        }, EnchantmentRegistry.REACTIVE_ENCHANTMENT, 3, 9000));

        registerApparatusRecipe(new SpellWriteRecipe());
    }

    public static void registerApparatusRecipe(IEnchantingRecipe recipe){
        ArsNouveauAPI.getInstance().getEnchantingApparatusRecipes().add(recipe);
    }

    public static void registerSpells(){
        registerSpell(GlyphLib.MethodProjectileID, new MethodProjectile());
        registerSpell(GlyphLib.MethodTouchID, new MethodTouch());
        registerSpell(GlyphLib.MethodSelfID, new MethodSelf());
        registerSpell(GlyphLib.EffectBreakID, new EffectBreak());
        registerSpell(GlyphLib.EffectHarmID, new EffectHarm());
        registerSpell(GlyphLib.EffectIgniteID, new EffectIgnite());
        registerSpell(GlyphLib.EffectPhantomBlockID, new EffectPhantomBlock());
        registerSpell(GlyphLib.EffectHealID, new EffectHeal());
        registerSpell(GlyphLib.EffectGrowID, new EffectGrow());
        registerSpell(GlyphLib.EffectKnockbackID, new EffectKnockback());
        registerSpell(GlyphLib.EffectHasteID, new EffectHaste());
        registerSpell(GlyphLib.EffectLightID, new EffectLight());
        registerSpell(GlyphLib.EffectDispelID, new EffectDispel());
        registerSpell(GlyphLib.EffectFreezeID, new EffectFreeze());
        registerSpell(GlyphLib.EffectLaunchID, new EffectLaunch());
        registerSpell(GlyphLib.EffectPullID, new EffectPull());
        registerSpell(GlyphLib.EffectBlinkID, new EffectBlink());
        registerSpell(GlyphLib.EffectExplosionID, new EffectExplosion());
        registerSpell(GlyphLib.EffectLightningID, new EffectLightning());
        registerSpell(GlyphLib.EffectSlowfallID, new EffectSlowfall());
        registerSpell(GlyphLib.EffectShieldID, new EffectShield());
        registerSpell(GlyphLib.EffectAquatic, new EffectAquatic());
        registerSpell(GlyphLib.EffectFangsID, new EffectFangs());
        registerSpell(GlyphLib.EffectSummonVexID, new EffectSummonVex());
        registerSpell(GlyphLib.EffectStrength, new EffectStrength());
        registerSpell(GlyphLib.AugmentAccelerateID, new AugmentAccelerate());
        registerSpell(GlyphLib.AugmentSplitID, new AugmentSplit());
        registerSpell(GlyphLib.AugmentAmplifyID, new AugmentAmplify());
        registerSpell(GlyphLib.AugmentAOEID, new AugmentAOE());
        registerSpell(GlyphLib.AugmentExtendTimeID, new AugmentExtendTime());
        registerSpell(GlyphLib.AugmentPierceID, new AugmentPierce());
        registerSpell(GlyphLib.AugmentDampenID, new AugmentDampen());
        registerSpell(GlyphLib.AugmentExtractID, new AugmentExtract());
        registerSpell(GlyphLib.AugmentFortuneID, new AugmentFortune());
        registerSpell(GlyphLib.EffectEnderChestID, new EffectEnderChest());
        registerSpell(GlyphLib.EffectHarvestID, new EffectHarvest());
        registerSpell(GlyphLib.EffectFellID, new EffectFell());
        registerSpell(GlyphLib.EffectPickupID, new EffectPickup());
        registerSpell(GlyphLib.EffectInteractID, new EffectInteract());
        registerSpell(GlyphLib.EffectPlaceBlockID, new EffectPlaceBlock());
        registerSpell(GlyphLib.MethodRuneID, new MethodRune());
        registerSpell(GlyphLib.EffectSnareID, new EffectSnare());
        registerSpell(GlyphLib.EffectSmeltID, new EffectSmelt());
        registerSpell(GlyphLib.EffectLeapID, new EffectLeap());
        registerSpell(GlyphLib.EffectDelayID, new EffectDelay());
        registerSpell(GlyphLib.EffectRedstoneID, new EffectRedstone());
        registerSpell(GlyphLib.EffectIntangibleID, new EffectIntangible());
        registerSpell(GlyphLib.EffectInvisibilityID, new EffectInvisibility());
        registerSpell(GlyphLib.AugmentDurationDown, new AugmentDurationDown());
        registerSpell(GlyphLib.EffectWitherID, new EffectWither());
        registerSpell(GlyphLib.EffectExchangeID, new EffectExchange());
        registerSpell(GlyphLib.EffectCraftID, new EffectCraft());
        registerSpell(GlyphLib.EffectFlareID, new EffectFlare());
        registerSpell(GlyphLib.EffectColdSnapID, new EffectColdSnap());
        registerSpell(GlyphLib.EffectConjureWaterID, new EffectConjureWater());
        registerSpell(GlyphLib.EffectGravityID, new EffectGravity());
        registerSpell(GlyphLib.EffectCutID, new EffectCut());
        registerSpell(GlyphLib.EffectCrushID, new EffectCrush());
        registerStartingSpells();
    }

    public static void registerStartingSpells(){
        addStartingSpell(GlyphLib.MethodProjectileID);
        addStartingSpell(GlyphLib.MethodTouchID);
        addStartingSpell(GlyphLib.MethodSelfID);
        addStartingSpell(GlyphLib.EffectBreakID);
        addStartingSpell(GlyphLib.EffectHarmID);
    }

    public static void addStartingSpell(String spellTag){
        ArsNouveauAPI.getInstance().addStartingSpell(spellTag);
    }

    public static void registerSpell(String id, AbstractSpellPart spellPart){
        ArsNouveauAPI.getInstance().registerSpell(id, spellPart);
    }

    private APIRegistry(){}
}
