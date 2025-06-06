package com.hollingsworth.arsnouveau.client.registry;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleType;
import com.hollingsworth.arsnouveau.client.particle.*;
import com.hollingsworth.arsnouveau.client.particle.BubbleParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;


public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MODID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<ColorParticleTypeData>> GLOW_TYPE = PARTICLES.register("glow", GlowParticleType::new);
    public static final DeferredHolder<ParticleType<?>, ParticleType<ColoredDynamicTypeData>> LINE_TYPE = PARTICLES.register("line", LineParticleType::new);
    public static final DeferredHolder<ParticleType<?>, ParticleType<ColoredDynamicTypeData>> SPARKLE_TYPE = PARTICLES.register("sparkle", SparkleParticleType::new);
    public static final DeferredHolder<ParticleType<?>, ParticleType<HelixParticleTypeData>> HELIX_TYPE = PARTICLES.register(HelixParticleData.NAME, HelixParticleType::new);

    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> ALAKARK_BUBBLE_TYPE = PARTICLES.register("bubble", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> BUBBLE_CLONE_TYPE = PARTICLES.register("an_bubble", PropertyParticleType::new);

    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SMOKE_TYPE = PARTICLES.register("smoke", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SNOW_TYPE = PARTICLES.register("snow", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> NEW_GLOW_TYPE = PARTICLES.register("new_glow", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> LEAF_TYPE = PARTICLES.register("leaf", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> DRIPPING_WATER = PARTICLES.register("dripping_water", PropertyParticleType::new);

    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> END_ROD = PARTICLES.register("end_rod", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> GLOW_SQUID = PARTICLES.register("glow_squid", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> GLOW_INK = PARTICLES.register("glow_ink", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> CRIT = PARTICLES.register("crit", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> ENCHANT = PARTICLES.register("enchant", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SPIT = PARTICLES.register("spit", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> DUST_PLUME = PARTICLES.register("dust_plume", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SMALL_GUST = PARTICLES.register("small_gust", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> BIG_GUST = PARTICLES.register("big_gust", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> DRAGON_BREATH = PARTICLES.register("dragon_breath", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> ENCHANTED_HIT = PARTICLES.register("enchanted_hit", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SONIC_BOOM = PARTICLES.register("sonic_boom", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> FIREWORK = PARTICLES.register("firework", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> FLAME = PARTICLES.register("flame", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> INFESTED = PARTICLES.register("infested", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SCULK_SOUL = PARTICLES.register("sculk_soul", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SCULK_CHARGE_POP = PARTICLES.register("sculk_charge_pop", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SCULK_CHARGE = PARTICLES.register("sculk_charge", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SOUL = PARTICLES.register("soul", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SOUL_FIRE_FLAME = PARTICLES.register("soul_fire_flame", PropertyParticleType::new);

    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> HAPPY_VILLAGER = PARTICLES.register("happy_villager", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> COMPOSTER = PARTICLES.register("composter", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> HEART = PARTICLES.register("heart", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> INSTANT_EFFECT = PARTICLES.register("instant_effect", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> ITEM_COBWEB = PARTICLES.register("item_cobweb", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> LARGE_SMOKE = PARTICLES.register("large_smoke", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> LAVA = PARTICLES.register("lava", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> MYCELIUM = PARTICLES.register("mycelium", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> NOTE = PARTICLES.register("note", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> POOF = PARTICLES.register("poof", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SPLASH = PARTICLES.register("splash", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SMOKE = PARTICLES.register("minecraft_smoke", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> WHITE_SMOKE = PARTICLES.register("white_smoke", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SNEEZE = PARTICLES.register("sneeze", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SQUID_INK = PARTICLES.register("squid_ink", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SWEEP_ATTACK = PARTICLES.register("sweep_attack", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> TOTEM_OF_UNDYING = PARTICLES.register("totem_of_undying", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> WITCH = PARTICLES.register("witch", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> BUBBLE_POP = PARTICLES.register("bubble_pop", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> NAUTILUS = PARTICLES.register("nautilus", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> CAMPFIRE_COSY_SMOKE = PARTICLES.register("campfire_cosy_smoke", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> CAMPFIRE_SIGNAL_SMOKE = PARTICLES.register("campfire_signal_smoke", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> ASH = PARTICLES.register("ash", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> CRIMSON_SPORE = PARTICLES.register("crimson_spore", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> WARPED_SPORE = PARTICLES.register("warped_spore", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> REVERSE_PORTAL = PARTICLES.register("reverse_portal", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> WHITE_ASH = PARTICLES.register("white_ash", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SMALL_FLAME = PARTICLES.register("small_flame", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SNOWFLAKE = PARTICLES.register("snowflake", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> ELECTRIC_SPARK = PARTICLES.register("electric_spark", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SCRAPE = PARTICLES.register("scrape", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> WAX = PARTICLES.register("wax", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> TRIAL_SPAWNER = PARTICLES.register("trial_spawner", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> OMINOUS_SPAWNING = PARTICLES.register("ominous_spawning", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> RAID_OMEN = PARTICLES.register("raid_omen", PropertyParticleType::new);

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Inner {
        @SubscribeEvent
        public static void registerFactories(RegisterParticleProvidersEvent evt) {

            evt.registerSpriteSet(GLOW_TYPE.get(), GlowParticleProvider::new);
            evt.registerSpriteSet(LINE_TYPE.get(), LineParticleProvider::new);
            evt.registerSpriteSet(SPARKLE_TYPE.get(), SparkleParticleProvider::new);
            evt.registerSpriteSet(HELIX_TYPE.get(), HelixParticleData::new);
            evt.registerSpriteSet(ALAKARK_BUBBLE_TYPE.get(), BubbleParticle.Provider::new);
            evt.registerSpriteSet(SMOKE_TYPE.get(), ANSmokeParticle.Provider::new);
            evt.registerSpriteSet(SNOW_TYPE.get(), SnowParticle.Provider::new);
            evt.registerSpriteSet(NEW_GLOW_TYPE.get(), NewGlowParticleProvider::new);
            evt.registerSpriteSet(BUBBLE_CLONE_TYPE.get(), ANBubbleParticle.Provider::new);
            evt.registerSpriteSet(LEAF_TYPE.get(), (sprites -> new PropParticle.Provider(LeafParticle::new, sprites)));
            evt.registerSpriteSet(DRIPPING_WATER.get(), (spites) -> new PropParticle.Provider(null, (type, level, x, y, z, xSpeed, ySpeed, zSpeed) -> {
                var particle = new FallingParticle(type, level, x, y, z, xSpeed, ySpeed, zSpeed);
                particle.type = Fluids.WATER;
                particle.landingSound = SoundEvents.POINTED_DRIPSTONE_DRIP_WATER;
                particle.pickSprite(Minecraft.getInstance().particleEngine.spriteSets.get(ResourceLocation.withDefaultNamespace("falling_water")));
                return particle;
            }));

            evt.registerSpriteSet(END_ROD.get(), (spites) -> new WrappedProvider(ParticleTypes.END_ROD, EndRodParticle.Provider::new));

            evt.registerSpriteSet(GLOW_SQUID.get(), (spites) -> new WrappedProvider(ParticleTypes.GLOW,  GlowParticle.GlowSquidProvider::new));

            evt.registerSpriteSet(GLOW_INK.get(), (spites) -> new WrappedProvider(ParticleTypes.GLOW_SQUID_INK, SquidInkParticle.GlowInkProvider::new));
            evt.registerSpriteSet(CRIT.get(), (spites) -> new WrappedProvider(ParticleTypes.CRIT, CritParticle.Provider::new));
            evt.registerSpriteSet(ENCHANT.get(), (spites) -> new WrappedProvider(ParticleTypes.ENCHANT, FlyTowardsPositionParticle.EnchantProvider::new));
            evt.registerSpriteSet(SPIT.get(), (spites) -> new WrappedProvider(ParticleTypes.SPIT, SpitParticle.Provider::new));
            evt.registerSpriteSet(DUST_PLUME.get(), (spites) -> new WrappedProvider(ParticleTypes.DUST_PLUME, DustPlumeParticle.Provider::new));
            evt.registerSpriteSet(SMALL_GUST.get(), (spites) -> new WrappedProvider(ParticleTypes.SMALL_GUST, GustParticle.SmallProvider::new));
            evt.registerSpriteSet(BIG_GUST.get(), (spites) -> new WrappedProvider(ParticleTypes.GUST, GustParticle.Provider::new));
            evt.registerSpriteSet(DRAGON_BREATH.get(), (spites) -> new WrappedProvider(ParticleTypes.DRAGON_BREATH, DragonBreathParticle.Provider::new));
            evt.registerSpriteSet(ENCHANTED_HIT.get(), (spites) -> new WrappedProvider(ParticleTypes.ENCHANTED_HIT, CritParticle.MagicProvider::new));
            evt.registerSpriteSet(SONIC_BOOM.get(), (spites) -> new WrappedProvider(ParticleTypes.SONIC_BOOM, SonicBoomParticle.Provider::new));
            evt.registerSpriteSet(FIREWORK.get(), (spites) -> new WrappedProvider(ParticleTypes.FIREWORK, FireworkParticles.SparkProvider::new));
            evt.registerSpriteSet(FLAME.get(), (spites) -> new WrappedProvider(ParticleTypes.FLAME, FlameParticle.Provider::new));
            evt.registerSpriteSet(INFESTED.get(), (spites) -> new WrappedProvider(ParticleTypes.INFESTED, SpellParticle.Provider::new));
            evt.registerSpriteSet(SCULK_SOUL.get(), (spites) -> new WrappedProvider(ParticleTypes.SCULK_SOUL, SoulParticle.EmissiveProvider::new));
            evt.registerSpriteSet(SOUL_FIRE_FLAME.get(), (spites) -> new WrappedProvider(ParticleTypes.SOUL_FIRE_FLAME, FlameParticle.Provider::new));
            evt.registerSpriteSet(SOUL.get(), (spites) -> new WrappedProvider(ParticleTypes.SOUL, SoulParticle.Provider::new));
//            evt.registerSpriteSet(SCULK_CHARGE_POP.get(), (spites) -> new WrappedProvider(ParticleTypes.SCULK_CHARGE_POP, SculkChargePopParticle.Provider::new));
//            evt.registerSpriteSet(SCULK_CHARGE.get(), (spites) -> new WrappedProvider(ParticleTypes.SCULK_CHARGE, SculkChargeParticle.Provider::new));
            evt.registerSpriteSet(HAPPY_VILLAGER.get(), (spites) -> new WrappedProvider(ParticleTypes.HAPPY_VILLAGER, SuspendedTownParticle.HappyVillagerProvider::new));
            evt.registerSpriteSet(COMPOSTER.get(), (spites) -> new WrappedProvider(ParticleTypes.COMPOSTER, SuspendedTownParticle.ComposterFillProvider::new));
            evt.registerSpriteSet(HEART.get(), (spites) -> new WrappedProvider(ParticleTypes.HEART, HeartParticle.Provider::new));
            evt.registerSpriteSet(INSTANT_EFFECT.get(), (spites) -> new WrappedProvider(ParticleTypes.INSTANT_EFFECT, SpellParticle.InstantProvider::new));
            evt.registerSpriteSet(ITEM_COBWEB.get(), (spites) -> new WrappedProvider(ParticleTypes.ITEM_COBWEB, new BreakingItemParticle.CobwebProvider()));
            evt.registerSpriteSet(LARGE_SMOKE.get(), (spites) -> new WrappedProvider(ParticleTypes.LARGE_SMOKE, LargeSmokeParticle.Provider::new));
            evt.registerSpriteSet(LAVA.get(), (spites) -> new WrappedProvider(ParticleTypes.LAVA, LavaParticle.Provider::new));
            evt.registerSpriteSet(MYCELIUM.get(), (spites) -> new WrappedProvider(ParticleTypes.MYCELIUM,  SuspendedTownParticle.Provider::new));
            evt.registerSpriteSet(NOTE.get(), (spites) -> new WrappedProvider(ParticleTypes.NOTE, NoteParticle.Provider::new));
            evt.registerSpriteSet(POOF.get(), (spites) -> new WrappedProvider(ParticleTypes.POOF, ExplodeParticle.Provider::new));
            evt.registerSpriteSet(SPLASH.get(), (spites) -> new WrappedProvider(ParticleTypes.SPLASH, SplashParticle.Provider::new));
            evt.registerSpriteSet(SMOKE.get(), (spites) -> new WrappedProvider(ParticleTypes.SMOKE, SmokeParticle.Provider::new));
            evt.registerSpriteSet(WHITE_SMOKE.get(), (spites) -> new WrappedProvider(ParticleTypes.WHITE_SMOKE, WhiteSmokeParticle.Provider::new));
            evt.registerSpriteSet(SNEEZE.get(), (spites) -> new WrappedProvider(ParticleTypes.SNEEZE, PlayerCloudParticle.SneezeProvider::new));
            evt.registerSpriteSet(SQUID_INK.get(), (spites) -> new WrappedProvider(ParticleTypes.SQUID_INK, SquidInkParticle.Provider::new));
            evt.registerSpriteSet(SWEEP_ATTACK.get(), (spites) -> new WrappedProvider(ParticleTypes.SWEEP_ATTACK, AttackSweepParticle.Provider::new));
            evt.registerSpriteSet(TOTEM_OF_UNDYING.get(), (spites) -> new WrappedProvider(ParticleTypes.TOTEM_OF_UNDYING, TotemParticle.Provider::new));
            evt.registerSpriteSet(WITCH.get(), (spites) -> new WrappedProvider(ParticleTypes.WITCH, SpellParticle.WitchProvider::new));
            evt.registerSpriteSet(BUBBLE_POP.get(), (spites) -> new WrappedProvider(ParticleTypes.BUBBLE_POP, BubblePopParticle.Provider::new));
            evt.registerSpriteSet(NAUTILUS.get(), (spites) -> new WrappedProvider(ParticleTypes.NAUTILUS, FlyTowardsPositionParticle.NautilusProvider::new));
            evt.registerSpriteSet(CAMPFIRE_COSY_SMOKE.get(), (spites) -> new WrappedProvider(ParticleTypes.CAMPFIRE_COSY_SMOKE,  CampfireSmokeParticle.CosyProvider::new));
            evt.registerSpriteSet(CAMPFIRE_SIGNAL_SMOKE.get(), (spites) -> new WrappedProvider(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, CampfireSmokeParticle.SignalProvider::new));
            evt.registerSpriteSet(ASH.get(), (spites) -> new WrappedProvider(ParticleTypes.ASH, AshParticle.Provider::new));
            evt.registerSpriteSet(CRIMSON_SPORE.get(), (spites) -> new WrappedProvider(ParticleTypes.CRIMSON_SPORE,  SuspendedParticle.CrimsonSporeProvider::new));
            evt.registerSpriteSet(WARPED_SPORE.get(), (spites) -> new WrappedProvider(ParticleTypes.WARPED_SPORE, SuspendedParticle.WarpedSporeProvider::new));
            evt.registerSpriteSet(REVERSE_PORTAL.get(), (spites) -> new WrappedProvider(ParticleTypes.REVERSE_PORTAL, ReversePortalParticle.Provider::new));
            evt.registerSpriteSet(WHITE_ASH.get(), (spites) -> new WrappedProvider(ParticleTypes.WHITE_ASH, WhiteAshParticle.Provider::new));
            evt.registerSpriteSet(SMALL_FLAME.get(), (spites) -> new WrappedProvider(ParticleTypes.SMALL_FLAME,  FlameParticle.SmallFlameProvider::new));
            evt.registerSpriteSet(SNOWFLAKE.get(), (spites) -> new WrappedProvider(ParticleTypes.SNOWFLAKE, SnowflakeParticle.Provider::new));
            evt.registerSpriteSet(ELECTRIC_SPARK.get(), (spites) -> new WrappedProvider(ParticleTypes.ELECTRIC_SPARK, GlowParticle.ElectricSparkProvider::new));
            evt.registerSpriteSet(SCRAPE.get(), (spites) -> new WrappedProvider(ParticleTypes.SCRAPE, GlowParticle.ScrapeProvider::new));
            evt.registerSpriteSet(WAX.get(), (spites) -> new WrappedProvider(ParticleTypes.WAX_OFF, GlowParticle.WaxOffProvider::new));
            evt.registerSpriteSet(TRIAL_SPAWNER.get(), (spites) -> new WrappedProvider(ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER, TrialSpawnerDetectionParticle.Provider::new));
            evt.registerSpriteSet(OMINOUS_SPAWNING.get(), (spites) -> new WrappedProvider(ParticleTypes.OMINOUS_SPAWNING, FlyStraightTowardsParticle.OminousSpawnProvider::new));
            evt.registerSpriteSet(RAID_OMEN.get(), (spites) -> new WrappedProvider(ParticleTypes.RAID_OMEN, SpellParticle.Provider::new));

        }
    }
}