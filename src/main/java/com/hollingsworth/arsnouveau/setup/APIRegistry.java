package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.ReactiveEnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.SpellWriteRecipe;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.ritual.*;
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

        registerApparatusRecipe(new SpellWriteRecipe());
    }

    public static void registerApparatusRecipe(IEnchantingRecipe recipe) {
        ArsNouveauAPI.getInstance().getEnchantingApparatusRecipes().add(recipe);
    }

    public static void registerSpells() {
        registerSpell(MethodProjectile.INSTANCE);
        registerSpell(MethodTouch.INSTANCE);
        registerSpell(MethodSelf.INSTANCE);
        registerSpell(EffectBreak.INSTANCE);
        registerSpell(EffectHarm.INSTANCE);
        registerSpell(EffectIgnite.INSTANCE);
        registerSpell(EffectPhantomBlock.INSTANCE);
        registerSpell(EffectHeal.INSTANCE);
        registerSpell(EffectGrow.INSTANCE);
        registerSpell(EffectKnockback.INSTANCE);
        registerSpell(EffectHaste.INSTANCE);
        registerSpell(EffectLight.INSTANCE);
        registerSpell(EffectDispel.INSTANCE);
        registerSpell(EffectFreeze.INSTANCE);
        registerSpell(EffectLaunch.INSTANCE);
        registerSpell(EffectPull.INSTANCE);
        registerSpell(EffectBlink.INSTANCE);
        registerSpell(EffectExplosion.INSTANCE);
        registerSpell(EffectLightning.INSTANCE);
        registerSpell(EffectSlowfall.INSTANCE);
        registerSpell(EffectShield.INSTANCE);
        registerSpell(EffectAquatic.INSTANCE);
        registerSpell(EffectFangs.INSTANCE);
        registerSpell(EffectSummonVex.INSTANCE);
        registerSpell(EffectStrength.INSTANCE);
        registerSpell(AugmentAccelerate.INSTANCE);
        registerSpell(AugmentSplit.INSTANCE);
        registerSpell(AugmentAmplify.INSTANCE);
        registerSpell(AugmentAOE.INSTANCE);
        registerSpell(AugmentExtendTime.INSTANCE);
        registerSpell(AugmentPierce.INSTANCE);
        registerSpell(AugmentDampen.INSTANCE);
        registerSpell(AugmentExtract.INSTANCE);
        registerSpell(AugmentFortune.INSTANCE);
        registerSpell(EffectEnderChest.INSTANCE);
        registerSpell(EffectHarvest.INSTANCE);
        registerSpell(EffectFell.INSTANCE);
        registerSpell(EffectPickup.INSTANCE);
        registerSpell(EffectInteract.INSTANCE);
        registerSpell(EffectPlaceBlock.INSTANCE);
        registerSpell(MethodRune.INSTANCE);
        registerSpell(EffectSnare.INSTANCE);
        registerSpell(EffectSmelt.INSTANCE);
        registerSpell(EffectLeap.INSTANCE);
        registerSpell(EffectDelay.INSTANCE);
        registerSpell(EffectRedstone.INSTANCE);
        registerSpell(EffectIntangible.INSTANCE);
        registerSpell(EffectInvisibility.INSTANCE);
        registerSpell(AugmentDurationDown.INSTANCE);
        registerSpell(EffectWither.INSTANCE);
        registerSpell(EffectExchange.INSTANCE);
        registerSpell(EffectCraft.INSTANCE);
        registerSpell(EffectFlare.INSTANCE);
        registerSpell(EffectColdSnap.INSTANCE);
        registerSpell(EffectConjureWater.INSTANCE);
        registerSpell(EffectGravity.INSTANCE);
        registerSpell(EffectCut.INSTANCE);
        registerSpell(EffectCrush.INSTANCE);
        registerSpell(EffectSummonWolves.INSTANCE);
        registerSpell(EffectSummonSteed.INSTANCE);
        registerSpell(EffectSummonDecoy.INSTANCE);
        registerSpell(EffectHex.INSTANCE);
        registerSpell(MethodUnderfoot.INSTANCE);
        registerSpell(EffectGlide.INSTANCE);

        registerRitual(new RitualDig());
        registerRitual(new RitualMoonfall());
        registerRitual(new RitualCloudshaper());
        registerRitual(new RitualSunrise());
        registerRitual(new RitualExpDrain());
        registerRitual(new RitualPillagerRaid());
        registerRitual(new RitualOvergrowth());
        registerRitual(new RitualBreed());
        registerRitual(new RitualHealing());
        registerRitual(new RitualWarp());
        registerRitual(new ScryingRitual());
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
