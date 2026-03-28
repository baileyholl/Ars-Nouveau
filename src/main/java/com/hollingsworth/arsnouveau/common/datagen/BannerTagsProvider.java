package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.BannerRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BannerPatternTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static com.hollingsworth.arsnouveau.ArsNouveau.prefix;

public class BannerTagsProvider extends BannerPatternTagsProvider {
    public BannerTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, ArsNouveau.MODID);
    }

    public static TagKey<BannerPattern> bannerTag = TagKey.create(Registries.BANNER_PATTERN, prefix("pattern_item/ars_stencil"));

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        tag(bannerTag)
                .addOptional(BannerRegistry.FIRE)
                .addOptional(BannerRegistry.WATER)
                .addOptional(BannerRegistry.EARTH)
                .addOptional(BannerRegistry.AIR)
                .addOptional(BannerRegistry.CONJURATION)
                .addOptional(BannerRegistry.MANIPULATION)
                .addOptional(BannerRegistry.ABJURATION)
                .addOptional(BannerRegistry.SPIRALS);

    }
}
