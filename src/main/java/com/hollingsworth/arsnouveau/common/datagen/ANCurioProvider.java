package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;

import java.util.concurrent.CompletableFuture;

public class ANCurioProvider extends CuriosDataProvider {
    public ANCurioProvider(PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> registries) {
        super(ArsNouveau.MODID, output, fileHelper, registries);
    }

    @Override
    public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
        ResourceLocation curioValidator = ResourceLocation.fromNamespaceAndPath("curios", "tag");

        this.createSlot("head").size(1);
        this.createSlot("ring").size(2);
        this.createSlot("belt").size(1);
        this.createSlot("necklace").size(1);
        this.createSlot("an_focus")
                .addValidator(curioValidator)
                .icon(ResourceLocation.fromNamespaceAndPath("curios", "slot/empty_curio_slot"))
                .order(1)
                .size(1);

        this.createEntities("an_curios").addPlayer()
                .addSlots("head", "ring", "belt", "necklace", "an_focus");
    }
}
