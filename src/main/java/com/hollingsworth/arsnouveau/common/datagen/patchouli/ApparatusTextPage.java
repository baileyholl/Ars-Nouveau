package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.RegistryObject;

public class ApparatusTextPage extends ApparatusPage{

    public ApparatusTextPage(String recipe) {
        super(recipe);
    }

    public ApparatusTextPage(ItemLike itemLike) {
        super(itemLike);
    }

    public ApparatusTextPage(RegistryObject<? extends ItemLike> itemLike) {
        super(itemLike);
    }

    @Override
    public ResourceLocation getType() {
        return ArsNouveau.prefix( "no_output_apparatus_recipe");
    }
}
