package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.WealdWalker;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

// GeckoLib 5: setCustomAnimations removed; getModelResource/getTextureResource now take GeoRenderState
// Head rotation / leg animation needs to be ported to addPerBoneRender or captureDefaultRenderState
// TODO: Port head/leg bone rotation to GeckoLib 5 addPerBoneRender pattern
public class WealdWalkerModel<W extends WealdWalker> extends GeoModel<W> {
    String type;

    public WealdWalkerModel(String type) {
        super();
        this.type = type;
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        // TODO: Store isBaby in renderState via DataTicket for entity-specific model selection
        return ArsNouveau.prefix(type + "_walker");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        // TODO: Store isBaby in renderState via DataTicket for entity-specific texture selection
        return ArsNouveau.prefix("textures/entity/" + type + "_walker.png");
    }

    @Override
    public Identifier getAnimationResource(W walker) {
        return walker.isBaby() ? ArsNouveau.prefix("weald_waddler_animations") : ArsNouveau.prefix("weald_walker_animations");
    }
}
