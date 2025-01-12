package com.hollingsworth.arsnouveau.common.ritual;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.FeaturePlacementRitual;
import com.hollingsworth.arsnouveau.api.ritual.features.BonemealFeature;
import com.hollingsworth.arsnouveau.api.ritual.features.IPlaceableFeature;
import com.hollingsworth.arsnouveau.api.ritual.features.PlaceBlockFeature;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FloweringRitual extends FeaturePlacementRitual {

    public List<Block> flowers;

    public FloweringRitual() {
        flowers = new ArrayList<>();
        flowers.addAll(Arrays.asList(Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP, Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY, Blocks.CORNFLOWER, Blocks.LILY_OF_THE_VALLEY, Blocks.SUNFLOWER, Blocks.LILAC, Blocks.PEONY, Blocks.ROSE_BUSH));

    }

    @Override
    public void addFeatures(List<IPlaceableFeature> features) {
        boolean isDesert = getConsumedItems().stream().anyMatch(i -> i.is(ItemTags.SAND));
        if(!isDesert) {
            features.add(new PlaceBlockFeature(1.5, 0.8, () -> flowers.get(getWorld().random.nextInt(flowers.size())).defaultBlockState()));
            features.add(new BonemealFeature(5, 0.8));
        }else{
            features.add(new PlaceBlockFeature(3, 0.04, Blocks.CACTUS::defaultBlockState));
            features.add(new PlaceBlockFeature(1.5, 0.1, Blocks.DEAD_BUSH::defaultBlockState));
        }
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        boolean isDesert = getConsumedItems().stream().anyMatch(i -> i.is(ItemTags.SAND));
        return super.canConsumeItem(stack) || (!isDesert && stack.is(ItemTags.SAND));
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix( RitualLib.FLOWERING);
    }

    @Override
    public String getLangName() {
        return "Flowering";
    }

    @Override
    public String getLangDescription() {
        return "Populates the nearby area with flowers and grass. Augmenting with a source gem will increase the radius by 1 for each gem. Augmenting with sand will replace flowers with Cactus and Dead Bushes.";
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(100, 100, 100);
    }
}
