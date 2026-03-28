package com.hollingsworth.arsnouveau.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

// 1.21.11: EntityRenderer is now 2 type params <T, S extends EntityRenderState>
// render(T, float, float, PoseStack, MultiBufferSource, int) removed; use submit()
// getTextureLocation(T) removed; no longer part of EntityRenderer
public class RenderBlank extends EntityRenderer<Entity, EntityRenderState> {
    private final Identifier entityTexture;

    public RenderBlank(EntityRendererProvider.Context renderManager, Identifier entityTexture) {
        super(renderManager);
        this.entityTexture = entityTexture;
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
