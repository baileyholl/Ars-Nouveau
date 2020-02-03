package com.hollingsworth.craftedmagic.client.renderer.entity;


import net.minecraft.client.renderer.entity.EntityRendererManager;

import net.minecraft.client.renderer.entity.TippedArrowRenderer;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.ResourceLocation;

public class RenderSpell extends TippedArrowRenderer {
    private final ResourceLocation entityTexture; // new ResourceLocation(ExampleMod.MODID, "textures/entity/spell_proj.png");


    public RenderSpell(EntityRendererManager renderManagerIn, ResourceLocation entityTexture)
    {
        super(renderManagerIn);
        this.entityTexture = entityTexture;

    }

    @Override
    public void doRender(ArrowEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(ArrowEntity entity) {
        return this.entityTexture;
    }
}