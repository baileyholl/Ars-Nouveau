package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class CompostablesProvider extends DataMapProvider {

    /**
     * Create a new provider.
     *
     * @param packOutput     the output location
     * @param lookupProvider a {@linkplain CompletableFuture} supplying the registries
     */
    protected CompostablesProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather() {
        builder(NeoForgeDataMaps.COMPOSTABLES)
                .add(BlockRegistry.BLAZING_SAPLING.getResourceLocation(), new Compostable(0.3f), false)
                .add(BlockRegistry.CASCADING_SAPLING.getResourceLocation(), new Compostable(0.3f), false)
                .add(BlockRegistry.FLOURISHING_SAPLING.getResourceLocation(), new Compostable(0.3f), false)
                .add(BlockRegistry.VEXING_SAPLING.getResourceLocation(), new Compostable(0.3f), false)
                .add(BlockRegistry.SOURCEBERRY_BUSH.getResourceLocation(), new Compostable(0.3f), false)
                .add(ItemsRegistry.MAGE_BLOOM.getResourceLocation(), new Compostable(0.65f), false)
                .add(BlockRegistry.MAGE_BLOOM_CROP.getResourceLocation(), new Compostable(0.65f), false)
                .add(BlockRegistry.BOMBEGRANTE_POD.getResourceLocation(), new Compostable(0.65f), false)
                .add(BlockRegistry.MENDOSTEEN_POD.getResourceLocation(), new Compostable(0.65f), false)
                .add(BlockRegistry.FROSTAYA_POD.getResourceLocation(), new Compostable(0.65f), false)
                .add(BlockRegistry.BASTION_POD.getResourceLocation(), new Compostable(0.65f), false)
                .add(BlockRegistry.FLOURISHING_LEAVES.getResourceLocation(), new Compostable(0.3f), false)
                .add(BlockRegistry.VEXING_LEAVES.getResourceLocation(), new Compostable(0.3f), false)
                .add(BlockRegistry.CASCADING_LEAVE.getResourceLocation(), new Compostable(0.3f), false)
                .add(BlockRegistry.BLAZING_LEAVES.getResourceLocation(), new Compostable(0.3f), false)
                .build();
    }
}
