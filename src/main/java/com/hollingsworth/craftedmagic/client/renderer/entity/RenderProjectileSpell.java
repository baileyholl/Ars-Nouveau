package com.hollingsworth.craftedmagic.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderTippedArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.util.ResourceLocation;

public class RenderProjectileSpell extends RenderTippedArrow {
    private final ResourceLocation entityTexture;

    public RenderProjectileSpell(final RenderManager renderManager, final ResourceLocation entityTexture) {
        super(renderManager);
        this.entityTexture = entityTexture;
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityTippedArrow entity) {
        return entityTexture;
    }
}
