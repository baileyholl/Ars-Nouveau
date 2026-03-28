package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.common.items.AnimBlockItem;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: getModelResource/getTextureResource now take GeoRenderState, not the animatable T
public class GenericItemModel extends GeoModel<AnimBlockItem> {
    GeoModel<?> model;

    public GenericItemModel(GeoModel<?> model) {
        this.model = model;
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model.getModelResource(renderState);
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return model.getTextureResource(renderState);
    }

    @Override
    public Identifier getAnimationResource(AnimBlockItem animBlockItem) {
        return model.getAnimationResource(null);
    }
}
