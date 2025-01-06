package com.hollingsworth.arsnouveau.client.renderer.entity;


import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RenderSpell extends EntityRenderer<EntityProjectileSpell> {
    private final ResourceLocation entityTexture;

    public RenderSpell(EntityRendererProvider.Context renderManagerIn, ResourceLocation entityTexture) {
        super(renderManagerIn);
        this.entityTexture = entityTexture;

    }

    @Override
    public void render(EntityProjectileSpell proj, float entityYaw, float partialTicks, PoseStack p_225623_4_, MultiBufferSource p_225623_5_, int p_225623_6_) {
    }


    @Override
    public ResourceLocation getTextureLocation(EntityProjectileSpell entity) {
        return this.entityTexture;
    }
}