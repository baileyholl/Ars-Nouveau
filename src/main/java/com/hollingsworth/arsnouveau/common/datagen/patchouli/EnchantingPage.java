package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;

public class EnchantingPage extends AbstractPage {

    public EnchantingPage(String recipe) {
        object.addProperty("recipe", recipe);
    }

    @Override
    public ResourceLocation getType() {
        return ArsNouveau.prefix("enchanting_recipe");
    }
}
