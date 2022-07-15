package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.WildenHunter;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class WildenHunterRenderer extends GenericRenderer<WildenHunter> {
    public WildenHunterRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WildenHunterModel());
    }

}
