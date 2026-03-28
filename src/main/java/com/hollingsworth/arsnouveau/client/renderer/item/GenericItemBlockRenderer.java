package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.common.items.AnimBlockItem;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

// GeckoLib 5.4.2 migration:
// - getRenderType(T, Identifier, MultiBufferSource, float) REMOVED
// - New signature: getRenderType(GeoRenderState, Identifier) from GeoRenderer interface
public class GenericItemBlockRenderer extends GeoItemRenderer<AnimBlockItem> {

    public boolean isTranslucent;

    public GenericItemBlockRenderer(GeoModel modelProvider) {
        super(new GenericItemModel(modelProvider));
    }

    public GenericItemBlockRenderer withTranslucency() {
        this.isTranslucent = true;
        return this;
    }

    @Override
    public RenderType getRenderType(GeoRenderState renderState, Identifier texture) {
        return this.isTranslucent ? RenderTypes.entityTranslucent(texture) : super.getRenderType(renderState, texture);
    }
}
