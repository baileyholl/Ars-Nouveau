package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.common.items.FlaskCannon;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: renderRecursively and GeoBone.setHidden() removed.
// TODO: Port per-bone potion-level visibility to the new captureDefaultRenderState + addPerBoneRender pattern.
public class FlaskCannonRenderer extends GeoItemRenderer<FlaskCannon> {

    public FlaskCannonRenderer(GeoModel<FlaskCannon> modelProvider) {
        super(modelProvider);
    }

    public FlaskCannonRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet, GeoModel<FlaskCannon> modelProvider) {
        super(dispatcher, modelSet, modelProvider);
    }

    // GeckoLib 5: getRenderType(R renderState, Identifier texture) — no animatable/bufferSource/partialTick
    @Override
    public RenderType getRenderType(GeoRenderState renderState, Identifier texture) {
        return RenderTypes.entityCutoutNoCull(texture);
    }
}
