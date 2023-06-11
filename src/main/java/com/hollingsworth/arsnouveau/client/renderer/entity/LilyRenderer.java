package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.Lily;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class LilyRenderer extends GenericRenderer<Lily> {
    public LilyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LilyModel());
    }


}
