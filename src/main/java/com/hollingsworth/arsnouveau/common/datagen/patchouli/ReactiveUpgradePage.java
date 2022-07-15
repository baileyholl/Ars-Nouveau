package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;

public class ReactiveUpgradePage extends AbstractPage {

    public ReactiveUpgradePage() {

    }

    @Override
    public ResourceLocation getType() {
        return new ResourceLocation(ArsNouveau.MODID, "reactive_enchant_upgrade");
    }
}
