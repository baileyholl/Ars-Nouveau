package com.hollingsworth.arsnouveau.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GenericRenderer extends GeoEntityRenderer {
    public GenericRenderer(EntityRendererManager renderManager, AnimatedGeoModel model){
        super(renderManager, model);
    }
}
