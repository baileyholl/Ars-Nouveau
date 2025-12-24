package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PaintingVariantTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.JsonCodecProvider;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PaintingProvider extends JsonCodecProvider<PaintingVariant> {
    public PaintingProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, PackOutput.Target.DATA_PACK, "painting_variant", PackType.SERVER_DATA, PaintingVariant.DIRECT_CODEC, lookupProvider, ArsNouveau.MODID, existingFileHelper);
    }

    @Override
    protected void gather() {
        add("starbuncle", 1, 1);
        add("resting_drygmy", 4, 4);
    }

    public void add(String name, int width, int height) {
        ResourceLocation rl = ArsNouveau.prefix(name);
        this.unconditional(
                rl,
                new PaintingVariant(width, height, rl)
        );
    }
}
