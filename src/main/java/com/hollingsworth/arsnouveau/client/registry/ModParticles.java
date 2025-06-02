package com.hollingsworth.arsnouveau.client.registry;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleType;
import com.hollingsworth.arsnouveau.client.particle.*;
import com.hollingsworth.arsnouveau.client.particle.BubbleParticle;
import com.hollingsworth.arsnouveau.client.particle.SmokeParticle;
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
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> CUSTOM_TYPE = PARTICLES.register("custom", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SMOKE_TYPE = PARTICLES.register("smoke", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SNOW_TYPE = PARTICLES.register("snow", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> NEW_GLOW_TYPE = PARTICLES.register("new_glow", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> LEAF_TYPE = PARTICLES.register("leaf", PropertyParticleType::new);
    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> DRIPPING_WATER = PARTICLES.register("dripping_water", PropertyParticleType::new);

    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> DRIPPING_LAVA = PARTICLES.register("dripping_lava", PropertyParticleType::new);

    public static final DeferredHolder<ParticleType<?>, PropertyParticleType> SPORE_BLOSSOM = PARTICLES.register("spore_blossom", PropertyParticleType::new);
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


    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Inner {
        @SubscribeEvent
        public static void registerFactories(RegisterParticleProvidersEvent evt) {

            evt.registerSpriteSet(GLOW_TYPE.get(), GlowParticleProvider::new);
            evt.registerSpriteSet(LINE_TYPE.get(), LineParticleProvider::new);
            evt.registerSpriteSet(SPARKLE_TYPE.get(), SparkleParticleProvider::new);
            evt.registerSpriteSet(HELIX_TYPE.get(), HelixParticleData::new);
            evt.registerSpriteSet(ALAKARK_BUBBLE_TYPE.get(), BubbleParticle.Provider::new);
            evt.registerSpriteSet(CUSTOM_TYPE.get(), CustomParticle.Provider::new);
            evt.registerSpriteSet(SMOKE_TYPE.get(), SmokeParticle.Provider::new);
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
        }
    }
}