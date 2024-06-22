package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.RegistryObject;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class ApparatusPage extends AbstractPage {

    public ApparatusPage(String recipe) {
        this.object.addProperty("recipe", recipe);
    }

    public ApparatusPage(ItemLike itemLike) {
        this(getRegistryName(itemLike.asItem()).toString());
    }

    public ApparatusPage(RegistryObject<? extends ItemLike> itemLike) {
        this(itemLike.get());
    }

    @Override
    public ResourceLocation getType() {
        return ArsNouveau.prefix( "apparatus_recipe");
    }
}
