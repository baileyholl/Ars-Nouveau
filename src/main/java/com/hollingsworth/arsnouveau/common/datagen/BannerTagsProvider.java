package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.BannerRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BannerPatternTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.hollingsworth.arsnouveau.ArsNouveau.prefix;

public class BannerTagsProvider extends BannerPatternTagsProvider {
    public BannerTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, ArsNouveau.MODID, existingFileHelper);
    }

    public static TagKey<BannerPattern> bannerTag = TagKey.create(Registries.BANNER_PATTERN, prefix("pattern_item/ars_stencil"));

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        tag(bannerTag)
                .addOptional(BannerRegistry.FIRE.location())
                .addOptional(BannerRegistry.WATER.location())
                .addOptional(BannerRegistry.EARTH.location())
                .addOptional(BannerRegistry.AIR.location())
                .addOptional(BannerRegistry.CONJURATION.location())
                .addOptional(BannerRegistry.MANIPULATION.location())
                .addOptional(BannerRegistry.ABJURATION.location())
                .addOptional(BannerRegistry.SPIRALS.location());

    }
}
