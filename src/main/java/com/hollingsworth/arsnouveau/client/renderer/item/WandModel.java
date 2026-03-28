package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.Wand;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class WandModel extends GeoModel<Wand> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return ArsNouveau.prefix("wand");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return ArsNouveau.prefix("textures/item/wand.png");
    }

    @Override
    public Identifier getAnimationResource(Wand wand) {
        return ArsNouveau.prefix("wand_animation");
    }
}
