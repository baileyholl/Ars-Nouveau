package com.hollingsworth.craftedmagic.client.renderer.entity;


import com.hollingsworth.craftedmagic.ExampleMod;
import com.hollingsworth.craftedmagic.entity.EntityProjectileSpell;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class RenderSpell extends EntityRenderer<EntityProjectileSpell>
{
    private static final ResourceLocation EXPERIENCE_ORB_TEXTURES =  new ResourceLocation(ExampleMod.MODID, "textures/entity/spell_proj.png");

    public RenderSpell(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn);
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;

    }


    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityProjectileSpell entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (!this.renderOutlines)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translated((float)x, (float)y, (float)z);

            this.bindEntityTexture(entity);
            RenderHelper.enableStandardItemLighting();
            int i = entity.getTextureByXP();
            float f = (float)(i % 4 * 16 + 0) / 64.0F;
            float f1 = (float)(i % 4 * 16 + 16) / 64.0F;
            float f2 = (float)(i / 4 * 16 + 0) / 64.0F;
            float f3 = (float)(i / 4 * 16 + 16) / 64.0F;
            float f4 = 1.0F;
            float f5 = 0.5F;
            float f6 = 0.25F;
            int j = entity.getBrightnessForRender();
            int k = j % 65536;
            int l = j / 65536;

            //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)k, (float)l);

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            float f8 = 255.0F;
            float f9 = ((float)entity.xpColor + partialTicks) / 2.0F;
            l = (int)((MathHelper.sin(f9 + 0.0F) + 1.0F) * 0.5F * 255.0F);
            int i1 = 255;
            int j1 = (int)((MathHelper.sin(f9 + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
            GlStateManager.translated(0.0F, 0.1F, 0.0F);
            GlStateManager.rotated(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotated((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            float f7 = 0.3F;
            GlStateManager.scaled(0.3F, 0.3F, 0.3F);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
            Random rand = new Random();
            int red = rand.nextInt(255);
            int green = rand.nextInt(255);
            int blue = rand.nextInt(255);
            int alpha = rand.nextInt(128);
//            bufferbuilder.pos(-0.5D, -0.25D, 0.0D).tex((double)f, (double)f3).color(l, 255, j1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
//            bufferbuilder.pos(0.5D, -0.25D, 0.0D).tex((double)f1, (double)f3).color(l, 255, j1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
//            bufferbuilder.pos(0.5D, 0.75D, 0.0D).tex((double)f1, (double)f2).color(l, 255, j1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
//            bufferbuilder.pos(-0.5D, 0.75D, 0.0D).tex((double)f, (double)f2).color(l, 255, j1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();

            bufferbuilder.pos(-0.5D, -0.25D, 0.0D).tex((double)f, (double)f3).color(l, green, j1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
            bufferbuilder.pos(0.5D, -0.25D, 0.0D).tex((double)f1, (double)f3).color(l, green, j1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
            bufferbuilder.pos(0.5D, 0.75D, 0.0D).tex((double)f1, (double)f2).color(l, green, j1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
            bufferbuilder.pos(-0.5D, 0.75D, 0.0D).tex((double)f, (double)f2).color(l, green, j1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
            tessellator.draw();
            GlStateManager.disableBlend();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityProjectileSpell entity)
    {
        return EXPERIENCE_ORB_TEXTURES;
    }
}