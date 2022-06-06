package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.WildenStalker;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class WildenStalkerRenderer extends GenericRenderer<WildenStalker> {
    public WildenStalkerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WildenStalkerModel());
    }

}
