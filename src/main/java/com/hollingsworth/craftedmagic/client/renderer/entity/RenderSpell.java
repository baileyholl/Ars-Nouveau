package com.hollingsworth.craftedmagic.client.renderer.entity;


import com.hollingsworth.craftedmagic.ExampleMod;
import com.hollingsworth.craftedmagic.entity.EntityProjectileSpell;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;

import net.minecraft.client.renderer.entity.TippedArrowRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


import java.util.Random;

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