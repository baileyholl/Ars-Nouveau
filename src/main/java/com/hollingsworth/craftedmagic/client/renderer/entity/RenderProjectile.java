package com.hollingsworth.craftedmagic.client.renderer.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderProjectile extends Render {

    private final float scale;
    private ResourceLocation texture;

    public RenderProjectile(RenderManager manager, float scale) {
        super(manager);
        this.scale = scale;
    }


    public RenderProjectile(RenderManager manager, float scale,
                            ResourceLocation textureLoc) {
        super(manager);
        this.scale = scale;
        setTexture(textureLoc);
    }


    public RenderProjectile setTexture(ResourceLocation textureLoc) {
        texture = textureLoc;
        return this;
    }


    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(Entity entity, double x, double y, double z,
                         float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        bindEntityTexture(entity);
        GlStateManager.translate(x, y, z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(scale, scale, scale);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.rotate(180.0F - renderManager.playerViewY,
                0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        if (renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        bufferbuilder.pos(-0.5D, -0.25D, 0.0D)
                .tex(0.0, 1.0)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        bufferbuilder.pos(0.5D, -0.25D, 0.0D)
                .tex(1.0, 1.0)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        bufferbuilder.pos(0.5D, 0.75D, 0.0D)
                .tex(1.0, 0.0)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        bufferbuilder.pos(-0.5D, 0.75D, 0.0D)
                .tex(0.0, 0.0)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        tessellator.draw();

        if(renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }




    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return texture;
    }
}
