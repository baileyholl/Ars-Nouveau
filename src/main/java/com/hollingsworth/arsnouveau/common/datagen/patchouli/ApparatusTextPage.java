package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;


public class ApparatusTextPage extends ApparatusPage {

    public ApparatusTextPage(String recipe) {
        super(recipe);
    }

    public ApparatusTextPage(ItemLike itemLike) {
        super(itemLike);
    }

    @Override
    public Identifier getType() {
        return ArsNouveau.prefix("no_output_apparatus_recipe");
    }
}
