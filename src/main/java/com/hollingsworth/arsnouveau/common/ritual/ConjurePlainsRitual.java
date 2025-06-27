package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.ConjureBiomeRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ConjurePlainsRitual extends ConjureBiomeRitual {
    boolean isSnowy;

    public ConjurePlainsRitual() {
        super(Biomes.PLAINS);
    }

    @Override
    public void onStart(@Nullable Player player) {
        super.onStart(player);
        isSnowy = getConsumedItems().stream().anyMatch(i -> i.is(BlockRegistry.FROSTAYA_POD.asItem()));
        if (isSnowy) {
            biome = Biomes.SNOWY_PLAINS;
        }
    }

    @Override
    public BlockState stateForPos(BlockPos nextPos) {
        return nextPos.getY() == getPos().getY() - 1 ? Blocks.GRASS_BLOCK.defaultBlockState() : Blocks.DIRT.defaultBlockState();
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        boolean frostaya = getConsumedItems().stream().anyMatch(i -> i.is(BlockRegistry.FROSTAYA_POD.asItem()));
        return super.canConsumeItem(stack) || (stack.is(BlockRegistry.FROSTAYA_POD.asItem()) && !frostaya);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix(RitualLib.PLAINS);
    }

    @Override
    public String getLangName() {
        return "Conjure Island: Plains";
    }

    @Override
    public String getLangDescription() {
        return "Creates an island of grass and dirt in a circle around the ritual, converting the area to Plains. Augmenting with a Frostaya with convert to Snow Plains. The island will generate with a radius of 7 blocks. Augmenting the ritual with Source Gems will increase the radius by 1 for each gem. Source must be provided nearby as blocks are generated.";
    }

    @Override
    public ParticleColor getCenterColor() {
        return isSnowy || didConsumeItem(BlockRegistry.FROSTAYA_POD) ? new ParticleColor(100, 100, 150) : new ParticleColor(100, 255, 100);
    }

    @Override
    public void write(HolderLookup.Provider provider, CompoundTag tag) {
        super.write(provider, tag);
        tag.putBoolean("isSnowy", isSnowy);
    }

    @Override
    public void read(HolderLookup.Provider provider, CompoundTag tag) {
        super.read(provider, tag);
        isSnowy = tag.getBoolean("isSnowy");
        if (isSnowy) {
            biome = Biomes.SNOWY_PLAINS;
        }
    }
}
