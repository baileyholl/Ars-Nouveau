package com.hollingsworth.arsnouveau.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderBlank extends EntityRenderer {
    private final ResourceLocation entityTexture; // new ResourceLocation(ExampleMod.MODID, "textures/entity/spell_proj.png");

    protected RenderBlank(EntityRendererManager renderManager, ResourceLocation entityTexture) {
        super(renderManager);
        this.entityTexture = entityTexture;
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return entityTexture;
    }
}
