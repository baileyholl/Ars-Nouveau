package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.perk.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.scrying.CompoundScryer;
import com.hollingsworth.arsnouveau.api.scrying.IScryer;
import com.hollingsworth.arsnouveau.api.scrying.SingleBlockScryer;
import com.hollingsworth.arsnouveau.api.scrying.TagScryer;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.familiars.*;
import com.hollingsworth.arsnouveau.common.perk.*;
import com.hollingsworth.arsnouveau.common.ritual.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.*;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;

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
        registerRitual(new RitualHarvest());
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
        registerSpell(EffectInfuse.INSTANCE);

        registerScryer(SingleBlockScryer.INSTANCE);
        registerScryer(CompoundScryer.INSTANCE);
        registerScryer(TagScryer.INSTANCE);

        registerPerk(StarbunclePerk.INSTANCE);
        registerPerk(DepthsPerk.INSTANCE);
        registerPerk(FeatherPerk.INSTANCE);
        registerPerk(GlidingPerk.INSTANCE);
        registerPerk(JumpHeightPerk.INSTANCE);
        registerPerk(LootingPerk.INSTANCE);
        registerPerk(MagicCapacityPerk.INSTANCE);
        registerPerk(MagicResistPerk.INSTANCE);
        registerPerk(PotionDurationPerk.INSTANCE);
        registerPerk(RepairingPerk.INSTANCE);
        registerPerk(SaturationPerk.INSTANCE);
        registerPerk(SpellDamagePerk.INSTANCE);
//        registerPerk(BondedPerk.INSTANCE);
        registerPerk(ChillingPerk.INSTANCE);
        registerPerk(IgnitePerk.INSTANCE);
        registerPerk(TotemPerk.INSTANCE);
        registerPerk(VampiricPerk.INSTANCE);
    }

    public static void postInit() {
        ArsNouveauAPI api = ArsNouveauAPI.getInstance();
        api.getEnchantingRecipeTypes().add(RecipeRegistry.APPARATUS_TYPE.get());
        api.getEnchantingRecipeTypes().add(RecipeRegistry.ENCHANTMENT_TYPE.get());
        api.getEnchantingRecipeTypes().add(RecipeRegistry.REACTIVE_TYPE.get());
        api.getEnchantingRecipeTypes().add(RecipeRegistry.SPELL_WRITE_TYPE.get());
        api.getEnchantingRecipeTypes().add(RecipeRegistry.ARMOR_UPGRADE_TYPE.get());
        api.registerPerkProvider(ItemsRegistry.ARCHMAGE_BOOTS, stack -> new ArmorPerkHolder(stack, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.ONE, PerkSlot.TWO)
        )));
        api.registerPerkProvider(ItemsRegistry.ARCHMAGE_HOOD, stack -> new ArmorPerkHolder(stack, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.ONE, PerkSlot.TWO)
        )));
        api.registerPerkProvider(ItemsRegistry.ARCHMAGE_LEGGINGS, stack -> new ArmorPerkHolder(stack, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                Arrays.asList(PerkSlot.ONE, PerkSlot.ONE, PerkSlot.THREE)
        )));
        api.registerPerkProvider(ItemsRegistry.ARCHMAGE_ROBES, stack -> new ArmorPerkHolder(stack, Arrays.asList(
                List.of(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                Arrays.asList(PerkSlot.ONE, PerkSlot.ONE, PerkSlot.THREE)
        )));

        api.registerPerkProvider(ItemsRegistry.APPRENTICE_HOOD, stack -> new ArmorPerkHolder(stack, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                Arrays.asList(PerkSlot.ONE, PerkSlot.ONE, PerkSlot.THREE)
        )));
        api.registerPerkProvider(ItemsRegistry.APPRENTICE_BOOTS, stack -> new ArmorPerkHolder(stack, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.TWO)
        )));

        api.registerPerkProvider(ItemsRegistry.APPRENTICE_LEGGINGS, stack -> new ArmorPerkHolder(stack, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.THREE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.THREE)
        )));

        api.registerPerkProvider(ItemsRegistry.APPRENTICE_ROBES, stack -> new ArmorPerkHolder(stack, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.THREE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.THREE)
        )));


        api.registerPerkProvider(ItemsRegistry.NOVICE_BOOTS, stack -> new ArmorPerkHolder(stack, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.THREE)
        )));

        api.registerPerkProvider(ItemsRegistry.NOVICE_ROBES, stack -> new ArmorPerkHolder(stack, Arrays.asList(
                Arrays.asList(PerkSlot.TWO),
                Arrays.asList(PerkSlot.TWO, PerkSlot.THREE),
                Arrays.asList(PerkSlot.TWO, PerkSlot.TWO, PerkSlot.THREE)
        )));

        api.registerPerkProvider(ItemsRegistry.NOVICE_LEGGINGS, stack -> new ArmorPerkHolder(stack, Arrays.asList(
                Arrays.asList(PerkSlot.TWO),
                Arrays.asList(PerkSlot.TWO, PerkSlot.THREE),
                Arrays.asList(PerkSlot.TWO, PerkSlot.TWO, PerkSlot.THREE)
        )));

        api.registerPerkProvider(ItemsRegistry.NOVICE_HOOD, stack -> new ArmorPerkHolder(stack, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.THREE)
        )));

        SoundRegistry.DEFAULT_SPELL_SOUND = new SpellSound(SoundRegistry.DEFAULT_FAMILY.get(), Component.translatable("ars_nouveau.sound.default_family"));
        SoundRegistry.EMPTY_SPELL_SOUND = new SpellSound(SoundRegistry.EMPTY_SOUND_FAMILY.get(), Component.translatable("ars_nouveau.sound.empty"));
        SoundRegistry.GAIA_SPELL_SOUND = new SpellSound(SoundRegistry.GAIA_FAMILY.get(), Component.translatable("ars_nouveau.sound.gaia_family"));
        SoundRegistry.TEMPESTRY_SPELL_SOUND = new SpellSound(SoundRegistry.TEMPESTRY_FAMILY.get(), Component.translatable("ars_nouveau.sound.tempestry_family"));
        SoundRegistry.FIRE_SPELL_SOUND = new SpellSound(SoundRegistry.FIRE_FAMILY.get(), Component.translatable("ars_nouveau.sound.fire_family"));

        ArsNouveauAPI.getInstance().registerSpellSound(SoundRegistry.DEFAULT_SPELL_SOUND);
        ArsNouveauAPI.getInstance().registerSpellSound(SoundRegistry.EMPTY_SPELL_SOUND);
        ArsNouveauAPI.getInstance().registerSpellSound(SoundRegistry.GAIA_SPELL_SOUND);
        ArsNouveauAPI.getInstance().registerSpellSound(SoundRegistry.TEMPESTRY_SPELL_SOUND);
        ArsNouveauAPI.getInstance().registerSpellSound(SoundRegistry.FIRE_SPELL_SOUND);
    }

    public static void registerFamiliar(AbstractFamiliarHolder familiar) {
        ArsNouveauAPI.getInstance().registerFamiliar(familiar);
    }

    public static void registerSpell(AbstractSpellPart spellPart) {
        ArsNouveauAPI.getInstance().registerSpell(spellPart);
    }

    public static void registerPerk(IPerk perk){
        ArsNouveauAPI.getInstance().registerPerk(perk);
    }

    public static void registerScryer(IScryer scryer){
        ArsNouveauAPI.getInstance().registerScryer(scryer);
    }

    public static void registerRitual(AbstractRitual ritual) {
        ArsNouveauAPI.getInstance().registerRitual(ritual);
    }

    private APIRegistry() {
    }
}
