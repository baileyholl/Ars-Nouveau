package com.hollingsworth.arsnouveau.common.datagen;

import com.google.common.collect.ImmutableMap;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.neoforged.neoforge.client.data.SpriteSourceProvider;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AtlasProvider extends SpriteSourceProvider {
    public static final Map<Block, EnumMap<ChestType, Material>> MATERIALS;

    static {
        ImmutableMap.Builder<Block, EnumMap<ChestType, Material>> builder = ImmutableMap.builder();

        builder.put(BlockRegistry.ARCHWOOD_CHEST.get(), chestMaterial("archwood"));
        MATERIALS = builder.build();
    }

    public AtlasProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, ArsNouveau.MODID);
    }

    private static EnumMap<ChestType, Material> chestMaterial(String type) {
        EnumMap<ChestType, Material> map = new EnumMap<>(ChestType.class);

        // Texture paths match CHEST_MAPPER convention: entity/chest/<namespace>/<name>
        // So "texture": "ars_nouveau:<type>" in items/ special JSON → sprite ars_nouveau:entity/chest/<type>
        map.put(ChestType.SINGLE, Sheets.CHEST_MAPPER.apply(ArsNouveau.prefix(type)));
        map.put(ChestType.LEFT, Sheets.CHEST_MAPPER.apply(ArsNouveau.prefix(type + "_left")));
        map.put(ChestType.RIGHT, Sheets.CHEST_MAPPER.apply(ArsNouveau.prefix(type + "_right")));

        return map;
    }

    @Override
    protected void gather() {
        MATERIALS.values().stream().flatMap(e -> e.values().stream()).map(Material::texture)
                .forEach(resourceLocation -> this.atlas(net.minecraft.data.AtlasIds.CHESTS).addSource(new SingleFile(resourceLocation, Optional.empty())));
    }
}