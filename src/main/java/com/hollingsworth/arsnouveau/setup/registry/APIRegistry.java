package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.registry.*;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.scrying.CompoundScryer;
import com.hollingsworth.arsnouveau.api.scrying.IScryer;
import com.hollingsworth.arsnouveau.api.scrying.SingleBlockScryer;
import com.hollingsworth.arsnouveau.api.scrying.TagScryer;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.familiars.*;
import com.hollingsworth.arsnouveau.common.mob_jar.*;
import com.hollingsworth.arsnouveau.common.perk.*;
import com.hollingsworth.arsnouveau.common.ritual.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.spell.method.MethodUnderfoot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.Arrays;
import java.util.List;

public class APIRegistry {

    public static void setup() {
        if (!FMLEnvironment.production) //only in dev
            registerWip();
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
        registerSpell(EffectRune.INSTANCE);
        registerSpell(EffectFreeze.INSTANCE);
        registerSpell(EffectName.INSTANCE);
        registerSpell(EffectSummonUndead.INSTANCE);
        registerSpell(EffectFirework.INSTANCE);
        registerSpell(EffectToss.INSTANCE);
        registerSpell(EffectBounce.INSTANCE);
        registerSpell(AugmentSensitive.INSTANCE);
        registerSpell(EffectWindshear.INSTANCE);
        registerSpell(EffectEvaporate.INSTANCE);
        registerSpell(EffectLinger.INSTANCE);
        registerSpell(EffectSenseMagic.INSTANCE);
        registerSpell(EffectInfuse.INSTANCE);
        registerSpell(EffectRotate.INSTANCE);
        registerSpell(EffectWall.INSTANCE);
        registerSpell(EffectAnimate.INSTANCE);
        registerSpell(EffectBurst.INSTANCE);
        registerSpell(AugmentRandomize.INSTANCE);
        registerSpell(EffectOrbit.INSTANCE);
        registerSpell(EffectReset.INSTANCE);
        registerSpell(EffectWololo.INSTANCE);
        registerSpell(EffectRewind.INSTANCE);

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
        registerRitual(new RitualGravity());
        registerRitual(new RitualWildenSummoning());
        registerRitual(new RitualAnimalSummoning());
        registerRitual(new RitualBinding());
        registerRitual(new RitualAwakening());
        registerRitual(new RitualHarvest());
        registerRitual(new RitualMobCapture());
        registerRitual(new ConjurePlainsRitual());
        registerRitual(new ForestationRitual());
        registerRitual(new FloweringRitual());
        registerRitual(new ConjureDesertRitual());
        registerRitual(new DenySpawnRitual());

        registerFamiliar(new StarbuncleFamiliarHolder());
        registerFamiliar(new DrygmyFamiliarHolder());
        registerFamiliar(new WhirlisprigFamiliarHolder());
        registerFamiliar(new WixieFamiliarHolder());
        registerFamiliar(new BookwyrmFamiliarHolder());
        registerFamiliar(new AmethystFamiliarHolder());

        registerScryer(SingleBlockScryer.INSTANCE);
        registerScryer(CompoundScryer.INSTANCE);
        registerScryer(TagScryer.INSTANCE);

        registerPerk(EmptyPerk.INSTANCE);
        registerPerk(StarbunclePerk.INSTANCE);
        registerPerk(StepHeightPerk.INSTANCE);
        registerPerk(ImmolatePerk.INSTANCE);
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
        registerPerk(ChillingPerk.INSTANCE);
        registerPerk(IgnitePerk.INSTANCE);
        registerPerk(TotemPerk.INSTANCE);
        registerPerk(VampiricPerk.INSTANCE);
        registerPerk(KnockbackResistPerk.INSTANCE);

    }

    //register things only in dev, safe from production
    private static void registerWip() {
    }

    public static void postInit() {
        ArsNouveauAPI api = ArsNouveauAPI.getInstance();
        api.getEnchantingRecipeTypes().add(RecipeRegistry.APPARATUS_TYPE.get());
        api.getEnchantingRecipeTypes().add(RecipeRegistry.ENCHANTMENT_TYPE.get());
        api.getEnchantingRecipeTypes().add(RecipeRegistry.REACTIVE_TYPE.get());
        api.getEnchantingRecipeTypes().add(RecipeRegistry.SPELL_WRITE_TYPE.get());
        api.getEnchantingRecipeTypes().add(RecipeRegistry.ARMOR_UPGRADE_TYPE.get());
        PerkRegistry.registerPerkProvider(ItemsRegistry.BATTLEMAGE_BOOTS, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.ONE, PerkSlot.TWO)
        ));
        PerkRegistry.registerPerkProvider(ItemsRegistry.BATTLEMAGE_HOOD, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.ONE, PerkSlot.TWO)
        ));
        PerkRegistry.registerPerkProvider(ItemsRegistry.BATTLEMAGE_LEGGINGS, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                Arrays.asList(PerkSlot.ONE, PerkSlot.ONE, PerkSlot.THREE)
        ));
        PerkRegistry.registerPerkProvider(ItemsRegistry.BATTLEMAGE_ROBES, Arrays.asList(
                List.of(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                Arrays.asList(PerkSlot.ONE, PerkSlot.ONE, PerkSlot.THREE)
        ));

        PerkRegistry.registerPerkProvider(ItemsRegistry.ARCANIST_HOOD, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                Arrays.asList(PerkSlot.ONE, PerkSlot.ONE, PerkSlot.THREE)
        ));
        PerkRegistry.registerPerkProvider(ItemsRegistry.ARCANIST_BOOTS, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.TWO)
        ));

        PerkRegistry.registerPerkProvider(ItemsRegistry.ARCANIST_LEGGINGS, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.THREE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.THREE)
        ));

        PerkRegistry.registerPerkProvider(ItemsRegistry.ARCANIST_ROBES, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.THREE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.THREE)
        ));


        PerkRegistry.registerPerkProvider(ItemsRegistry.SORCERER_BOOTS, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.THREE)
        ));

        PerkRegistry.registerPerkProvider(ItemsRegistry.SORCERER_ROBES,Arrays.asList(
                Arrays.asList(PerkSlot.TWO),
                Arrays.asList(PerkSlot.TWO, PerkSlot.THREE),
                Arrays.asList(PerkSlot.TWO, PerkSlot.TWO, PerkSlot.THREE)
        ));

        PerkRegistry.registerPerkProvider(ItemsRegistry.SORCERER_LEGGINGS, Arrays.asList(
                Arrays.asList(PerkSlot.TWO),
                Arrays.asList(PerkSlot.TWO, PerkSlot.THREE),
                Arrays.asList(PerkSlot.TWO, PerkSlot.TWO, PerkSlot.THREE)
        ));

        PerkRegistry.registerPerkProvider(ItemsRegistry.SORCERER_HOOD, Arrays.asList(
                Arrays.asList(PerkSlot.ONE),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.THREE)
        ));


        SpellSoundRegistry.registerSpellSound(SoundRegistry.DEFAULT_SPELL_SOUND);
        SpellSoundRegistry.registerSpellSound(SoundRegistry.EMPTY_SPELL_SOUND);
        SpellSoundRegistry.registerSpellSound(SoundRegistry.GAIA_SPELL_SOUND);
        SpellSoundRegistry.registerSpellSound(SoundRegistry.TEMPESTRY_SPELL_SOUND);
        SpellSoundRegistry.registerSpellSound(SoundRegistry.FIRE_SPELL_SOUND);

        JarBehaviorRegistry.register(EntityType.ELDER_GUARDIAN, new ElderGuardianBehavior());
        JarBehaviorRegistry.register(EntityType.CREEPER, new CreeperBehavior());
        JarBehaviorRegistry.register(EntityType.CHICKEN, new ChickenBehavior());
        JarBehaviorRegistry.register(EntityType.VILLAGER, new VillagerBehavior());
        JarBehaviorRegistry.register(EntityType.SHEEP, new SheepBehavior());
        JarBehaviorRegistry.register(EntityType.FROG, new FrogBehavior());
        JarBehaviorRegistry.register(EntityType.PIGLIN, new PiglinBehavior());
        JarBehaviorRegistry.register(EntityType.GHAST, new GhastBehavior());
        JarBehaviorRegistry.register(EntityType.SQUID, new SquidBehavior());
        JarBehaviorRegistry.register(EntityType.GLOW_SQUID, new GlowSquidBehavior());
        JarBehaviorRegistry.register(EntityType.BLAZE, new BlazeBehavior());
        JarBehaviorRegistry.register(EntityType.PANDA, new PandaBehavior());
        JarBehaviorRegistry.register(EntityType.MOOSHROOM, new MooshroomBehavior());
        JarBehaviorRegistry.register(EntityType.ENDER_DRAGON, new DragonBehavior());
        JarBehaviorRegistry.register(EntityType.PUFFERFISH, new PufferfishBehavior());
        JarBehaviorRegistry.register(EntityType.ALLAY, new AllayBehavior());
        JarBehaviorRegistry.register(ModEntities.ENTITY_DUMMY.get(), new DecoyBehavior());
        JarBehaviorRegistry.register(EntityType.ITEM, new JarBehavior<>() {
            @Override
            public void use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, MobJarTile tile) {
                if (player.isShiftKeyDown()) {
                    tile.onDispel(player);
                }
            }
        });

        DynamicTooltipRegistry.register(DataComponentRegistry.REACTIVE_CASTER.get());
    }

    public static void registerFamiliar(AbstractFamiliarHolder familiar) {
        FamiliarRegistry.registerFamiliar(familiar);
    }

    public static void registerSpell(AbstractSpellPart spellPart) {
        GlyphRegistry.registerSpell(spellPart);
    }

    public static void registerPerk(IPerk perk){
        PerkRegistry.registerPerk(perk);
    }

    public static void registerScryer(IScryer scryer){
        ArsNouveauAPI.getInstance().registerScryer(scryer);
    }

    public static void registerRitual(AbstractRitual ritual) {
        RitualRegistry.registerRitual(ritual);
    }

    private APIRegistry() {
    }
}
