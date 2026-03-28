package com.hollingsworth.arsnouveau.client.renderer.entity.familiar;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarStarbuncle;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: setCustomAnimations removed; getModelResource/getTextureResource now take GeoRenderState
// Head rotation via bone manipulation needs to be done in captureDefaultRenderState / addPerBoneRender
// TODO: Port head rotation to GeckoLib 5 addPerBoneRender pattern
public class FamiliarStarbyModel<T extends FamiliarStarbuncle> extends GeoModel<T> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        // TODO: Retrieve model resource from renderState via DataTicket when captureDefaultRenderState is implemented
        return ArsNouveau.prefix("starbuncle");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        // TODO: Retrieve texture from renderState via DataTicket when captureDefaultRenderState is implemented
        return ArsNouveau.prefix("textures/entity/starbuncle_blue.png");
    }

    @Override
    public Identifier getAnimationResource(FamiliarStarbuncle carbuncle) {
        return ArsNouveau.prefix("starbuncle_animations");
    }

}
