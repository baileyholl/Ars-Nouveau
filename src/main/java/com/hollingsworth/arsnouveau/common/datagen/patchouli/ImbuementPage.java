package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import static com.hollingsworth.arsnouveau.api.RegistryHelper.getRegistryName;

public class ImbuementPage extends AbstractPage {

    public ImbuementPage(String recipe) {
        this.object.addProperty("recipe", recipe);
    }

    public ImbuementPage(ItemLike itemLike) {
        this(getRegistryName(itemLike.asItem()).toString());
    }

    @Override
    public ResourceLocation getType() {
        return new ResourceLocation("ars_nouveau:imbuement_recipe");
    }
}
