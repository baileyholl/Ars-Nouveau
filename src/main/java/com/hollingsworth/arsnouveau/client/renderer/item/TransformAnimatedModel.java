package com.hollingsworth.arsnouveau.client.renderer.item;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import javax.annotation.Nullable;

public abstract class TransformAnimatedModel<T extends GeoAnimatable> extends GeoModel<T> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        // Cast null to ItemDisplayContext to disambiguate the two getOrDefaultGeckolibData overloads
        return getModelResource(renderState.getOrDefaultGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE, (ItemDisplayContext) null));
    }

    public abstract Identifier getModelResource(@Nullable ItemDisplayContext transformType);

}
