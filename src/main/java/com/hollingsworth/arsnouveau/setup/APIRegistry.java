package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.scrying.CompoundScryer;
import com.hollingsworth.arsnouveau.api.scrying.SingleBlockScryer;
import com.hollingsworth.arsnouveau.api.scrying.TagScryer;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.familiars.*;
import com.hollingsworth.arsnouveau.common.ritual.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.*;

public class APIRegistry {

    public static void setup() {
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
        registerSpell(EffectLight.INSTANCE);
        registerSpell(EffectDispel.INSTANCE);
        registerSpell(EffectLaunch.INSTANCE);
        registerSpell(EffectPull.INSTANCE);
        registerSpell(EffectBlink.INSTANCE);
        registerSpell(EffectExplosion.INSTANCE);
        registerSpell(EffectLightning.INSTANCE);
        registerSpell(EffectSlowfall.INSTANCE);
        registerSpell(EffectAquatic.INSTANCE);
        registerSpell(EffectFangs.INSTANCE);
        registerSpell(EffectSummonVex.INSTANCE);
        registerSpell(AugmentAccelerate.INSTANCE);
        registerSpell(AugmentDecelerate.INSTANCE);
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
        registerSpell(MethodOrbit.INSTANCE);
        registerSpell(EffectRune.INSTANCE);
        registerSpell(EffectFreeze.INSTANCE);
        registerSpell(EffectName.INSTANCE);
        registerSpell(EffectSummonUndead.INSTANCE);
        registerRitual(new RitualDig());
        registerRitual(new RitualMoonfall());
        registerRitual(new RitualCloudshaper());
        registerRitual(new RitualSunrise());
        registerRitual(new RitualDisintegration());
        registerRitual(new RitualPillagerRaid());
        registerRitual(new RitualOvergrowth());
        registerRitual(new RitualBreed());
        registerRitual(new RitualHealing());
        registerRitual(new RitualWarp());
        registerRitual(new RitualScrying());
        registerRitual(new RitualFlight());
        registerRitual(new RitualWildenSummoning());
        registerRitual(new RitualBinding());
        registerRitual(new RitualAwakening());
        registerFamiliar(new StarbuncleFamiliarHolder());
        registerFamiliar(new DrygmyFamiliarHolder());
        registerFamiliar(new WhirlisprigFamiliarHolder());
        registerFamiliar(new WixieFamiliarHolder());
//        registerFamiliar(new JabberwogFamiliar());
        registerFamiliar(new BookwyrmFamiliarHolder());

        registerSpell(EffectFirework.INSTANCE);
        registerSpell(EffectToss.INSTANCE);
        registerSpell(EffectBounce.INSTANCE);
        registerSpell(AugmentSensitive.INSTANCE);
        registerSpell(EffectWindshear.INSTANCE);
        registerSpell(EffectEvaporate.INSTANCE);
        registerSpell(EffectLinger.INSTANCE);
        registerSpell(EffectSenseMagic.INSTANCE);
        ArsNouveauAPI api = ArsNouveauAPI.getInstance();
        api.registerScryer(SingleBlockScryer.INSTANCE);
        api.registerScryer(CompoundScryer.INSTANCE);
        api.registerScryer(TagScryer.INSTANCE);
    }

    public static void postInit() {
        ArsNouveauAPI api = ArsNouveauAPI.getInstance();
        api.getEnchantingRecipeTypes().add(RecipeRegistry.APPARATUS_TYPE.get());
        api.getEnchantingRecipeTypes().add(RecipeRegistry.ENCHANTMENT_TYPE.get());
        api.getEnchantingRecipeTypes().add(RecipeRegistry.REACTIVE_TYPE.get());
        api.getEnchantingRecipeTypes().add(RecipeRegistry.SPELL_WRITE_TYPE.get());
    }

    public static void registerFamiliar(AbstractFamiliarHolder familiar) {
        ArsNouveauAPI.getInstance().registerFamiliar(familiar);
    }

    public static void registerSpell(AbstractSpellPart spellPart) {
        ArsNouveauAPI.getInstance().registerSpell(spellPart);
    }

    public static void registerRitual(AbstractRitual ritual) {
        ArsNouveauAPI.getInstance().registerRitual(ritual);
    }

    private APIRegistry() {
    }
}
