package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.GiftStarbuncle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GiftStarbyRenderer extends GeoEntityRenderer<GiftStarbuncle> {
    public GiftStarbyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GiftStarbyModel());
    }

    @Override
    public RenderType getRenderType(GiftStarbuncle animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
