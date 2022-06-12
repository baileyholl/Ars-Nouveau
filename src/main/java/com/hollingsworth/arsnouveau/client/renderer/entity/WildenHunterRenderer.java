package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.WildenHunter;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

public class WildenHunterRenderer extends GenericRenderer<WildenHunter> {
    public WildenHunterRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WildenHunterModel());
    }

}
