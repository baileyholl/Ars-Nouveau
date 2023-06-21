package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.FlaskCannon;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;


public class FlaskCannonModel extends GeoModel<FlaskCannon> {
    @Override
    public ResourceLocation getModelResource(FlaskCannon object) {
        return new ResourceLocation(ArsNouveau.MODID, "geo/lingering_flask_cannon.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FlaskCannon object) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/items/lingering_flask_cannon.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FlaskCannon animatable) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/empty.json");
    }
}
