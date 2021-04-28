package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.ReactiveEnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.SpellWriteRecipe;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.ritual.RitualDig;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.*;
import net.minecraft.item.ItemStack;

public class APIRegistry {

    public static void registerApparatusRecipes() {
        registerApparatusRecipe(new ReactiveEnchantmentRecipe(new ItemStack[]{new ItemStack(ItemsRegistry.spellParchment),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentAmplifyID)),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentAmplifyID)),
                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentAmplifyID))}, 3000));
//
//        registerApparatusRecipe(new EnchantmentRecipe(new ItemStack[]{
//                new ItemStack(Items.BLAZE_POWDER),
//                new ItemStack(Items.BLAZE_POWDER),
//                new ItemStack(Items.BLAZE_POWDER),
//                new ItemStack(Items.BLAZE_POWDER),
//                new ItemStack(Items.GOLD_BLOCK),
//                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentExtendTimeID)),
//                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentAOEID)),
//                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentDampenID))
//        }, EnchantmentRegistry.REACTIVE_ENCHANTMENT, 2, 6000));
//
//        registerApparatusRecipe(new EnchantmentRecipe(new ItemStack[]{
//                ItemsRegistry.mythicalClay.getStack(),
//                ItemsRegistry.mythicalClay.getStack(),
//                ItemsRegistry.mythicalClay.getStack(),
//                ItemsRegistry.mythicalClay.getStack(),
//                new ItemStack(Items.ENDER_PEARL),
//                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentPierceID)),
//                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentExtractID)),
//                new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(GlyphLib.AugmentFortuneID))
//        }, EnchantmentRegistry.REACTIVE_ENCHANTMENT, 3, 9000));

        registerApparatusRecipe(new SpellWriteRecipe());
    }

    public static void registerApparatusRecipe(IEnchantingRecipe recipe) {
        ArsNouveauAPI.getInstance().getEnchantingApparatusRecipes().add(recipe);
    }

    public static void registerSpells() {
        registerSpell(new MethodProjectile());
        registerSpell(new MethodTouch());
        registerSpell(new MethodSelf());
        registerSpell(new EffectBreak());
        registerSpell(new EffectHarm());
        registerSpell(new EffectIgnite());
        registerSpell(new EffectPhantomBlock());
        registerSpell(new EffectHeal());
        registerSpell(new EffectGrow());
        registerSpell(new EffectKnockback());
        registerSpell(new EffectHaste());
        registerSpell(new EffectLight());
        registerSpell(new EffectDispel());
        registerSpell(new EffectFreeze());
        registerSpell(new EffectLaunch());
        registerSpell(new EffectPull());
        registerSpell(new EffectBlink());
        registerSpell(new EffectExplosion());
        registerSpell(new EffectLightning());
        registerSpell(new EffectSlowfall());
        registerSpell(new EffectShield());
        registerSpell(new EffectAquatic());
        registerSpell(new EffectFangs());
        registerSpell(new EffectSummonVex());
        registerSpell(new EffectStrength());
        registerSpell(new AugmentAccelerate());
        registerSpell(new AugmentSplit());
        registerSpell(new AugmentAmplify());
        registerSpell(new AugmentAOE());
        registerSpell(new AugmentExtendTime());
        registerSpell(new AugmentPierce());
        registerSpell(new AugmentDampen());
        registerSpell(new AugmentExtract());
        registerSpell(new AugmentFortune());
        registerSpell(new EffectEnderChest());
        registerSpell(new EffectHarvest());
        registerSpell(new EffectFell());
        registerSpell(new EffectPickup());
        registerSpell(new EffectInteract());
        registerSpell(new EffectPlaceBlock());
        registerSpell(new MethodRune());
        registerSpell(new EffectSnare());
        registerSpell(new EffectSmelt());
        registerSpell(new EffectLeap());
        registerSpell(new EffectDelay());
        registerSpell(new EffectRedstone());
        registerSpell(new EffectIntangible());
        registerSpell(new EffectInvisibility());
        registerSpell(new AugmentDurationDown());
        registerSpell(new EffectWither());
        registerSpell(new EffectExchange());
        registerSpell(new EffectCraft());
        registerSpell(new EffectFlare());
        registerSpell(new EffectColdSnap());
        registerSpell(new EffectConjureWater());
        registerSpell(new EffectGravity());
        registerSpell(new EffectCut());
        registerSpell(new EffectCrush());
        registerSpell(new EffectSummonWolves());
        registerSpell(new EffectSummonSteed());
        registerSpell(new EffectSummonDecoy());
        registerSpell(new EffectHex());
        registerSpell(new MethodUnderfoot());
        registerStartingSpells();

        registerRitual(new RitualDig());
    }

    public static void registerStartingSpells() {
        addStartingSpell(GlyphLib.MethodProjectileID);
        addStartingSpell(GlyphLib.MethodTouchID);
        addStartingSpell(GlyphLib.MethodSelfID);
        addStartingSpell(GlyphLib.EffectBreakID);
        addStartingSpell(GlyphLib.EffectHarmID);
    }

    public static void addStartingSpell(String spellTag) {
        ArsNouveauAPI.getInstance().addStartingSpell(spellTag);
    }

    public static void registerSpell(AbstractSpellPart spellPart) {
        ArsNouveauAPI.getInstance().registerSpell(spellPart.getTag(), spellPart);
    }

    public static void registerRitual(AbstractRitual ritual){
        ArsNouveauAPI.getInstance().registerRitual(ritual.getID(), ritual);
    }

    public static void registerSpell(String id, AbstractSpellPart spellPart) {
        ArsNouveauAPI.getInstance().registerSpell(id, spellPart);
    }

    private APIRegistry() {
    }
}
