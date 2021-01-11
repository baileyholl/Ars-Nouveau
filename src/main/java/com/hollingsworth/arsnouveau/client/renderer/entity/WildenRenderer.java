package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.WildenHunter;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class WildenRenderer extends GeoEntityRenderer<WildenHunter> {
    protected WildenRenderer(EntityRendererManager renderManager) {
        super(renderManager, new WildenHunterModel());
    }
}
