package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.Whirlisprig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class WhirlisprigRenderer extends TextureVariantRenderer<Whirlisprig> {

    public WhirlisprigRenderer(EntityRendererProvider.Context manager) {
        super(manager, new WhirlisprigModel<>());
    }

}
