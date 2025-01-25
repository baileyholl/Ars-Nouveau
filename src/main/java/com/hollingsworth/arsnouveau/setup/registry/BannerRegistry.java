package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BannerPattern;

public class BannerRegistry {

    public static final ResourceKey<BannerPattern> FIRE = create("fire");
    public static final ResourceKey<BannerPattern> WATER = create("water");
    public static final ResourceKey<BannerPattern> EARTH = create("earth");
    public static final ResourceKey<BannerPattern> AIR = create("air");
    public static final ResourceKey<BannerPattern> MANIPULATION = create("manipulation");
    public static final ResourceKey<BannerPattern> CONJURATION = create("conjuration");
    public static final ResourceKey<BannerPattern> ABJURATION = create("abjuration");
    public static final ResourceKey<BannerPattern> SPIRALS = create("spirals");

    private static ResourceKey<BannerPattern> create(String name) {
        return ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, name));
    }

    public static void bootstrapPatterns(BootstrapContext<BannerPattern> bannerPatternBootstrapContext) {
        register(bannerPatternBootstrapContext, FIRE);
        register(bannerPatternBootstrapContext, WATER);
        register(bannerPatternBootstrapContext, EARTH);
        register(bannerPatternBootstrapContext, AIR);
        register(bannerPatternBootstrapContext, MANIPULATION);
        register(bannerPatternBootstrapContext, CONJURATION);
        register(bannerPatternBootstrapContext, ABJURATION);
        register(bannerPatternBootstrapContext, SPIRALS);
    }

    public static void register(BootstrapContext<BannerPattern> context, ResourceKey<BannerPattern> resourceKey) {
        context.register(resourceKey, new BannerPattern(resourceKey.location(), "block.ars_nouveau.banner." + resourceKey.location().toShortLanguageKey()));
    }
}
