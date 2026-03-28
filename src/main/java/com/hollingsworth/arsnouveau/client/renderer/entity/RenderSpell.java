package com.hollingsworth.arsnouveau.client.renderer.entity;


import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;

// MC 1.21.11: EntityRenderer requires 2 type params <T, S extends EntityRenderState>
public class RenderSpell extends EntityRenderer<EntityProjectileSpell, EntityRenderState> {
    private final Identifier entityTexture;

    public RenderSpell(EntityRendererProvider.Context renderManagerIn, Identifier entityTexture) {
        super(renderManagerIn);
        this.entityTexture = entityTexture;
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        // Intentionally empty - spell projectile rendering is handled elsewhere.
    }

    // 1.21.11: EntityRenderer no longer has getTextureLocation(T entity) — removed @Override
    public Identifier getTextureLocation(EntityProjectileSpell entity) {
        return this.entityTexture;
    }
}
