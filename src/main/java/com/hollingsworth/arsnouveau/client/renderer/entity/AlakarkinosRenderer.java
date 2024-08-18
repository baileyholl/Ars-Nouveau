package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.Alakarkinos;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class AlakarkinosRenderer extends GeoEntityRenderer<Alakarkinos> {
    public AlakarkinosRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AlakarkinosModel());
    }
}
