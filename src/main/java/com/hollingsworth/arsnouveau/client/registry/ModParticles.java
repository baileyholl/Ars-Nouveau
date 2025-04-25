package com.hollingsworth.arsnouveau.client.registry;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleType;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ParticleTypeProperty;
import com.hollingsworth.arsnouveau.client.particle.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;


@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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

        ParticleTypeProperty.addType(new ParticleTypeProperty.ParticleData(BUBBLE_CLONE_TYPE.get(), false));
        ParticleTypeProperty.addType(new ParticleTypeProperty.ParticleData(SMOKE_TYPE.get(), false));
        ParticleTypeProperty.addType(new ParticleTypeProperty.ParticleData(SNOW_TYPE.get(), false));
        ParticleTypeProperty.addType(new ParticleTypeProperty.ParticleData(NEW_GLOW_TYPE.get(), true));
        ParticleTypeProperty.addType(new ParticleTypeProperty.ParticleData(CUSTOM_TYPE.get(), true));
        ParticleTypeProperty.addType(new ParticleTypeProperty.ParticleData(LEAF_TYPE.get(), true));
    }
}