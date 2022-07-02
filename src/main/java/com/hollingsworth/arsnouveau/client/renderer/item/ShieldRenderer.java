package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.common.items.EnchantersShield;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class ShieldRenderer extends FixedGeoItemRenderer<EnchantersShield> {
    public ShieldRenderer() {
        super(new ShieldModel());
    }

    @Override
    public RenderType getRenderType(Object animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }
}
