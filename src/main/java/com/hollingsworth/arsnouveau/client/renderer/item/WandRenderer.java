package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.common.items.Wand;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class WandRenderer extends FixedGeoItemRenderer<Wand> {
    public WandRenderer() {
        super(new WandModel());
    }

    @Override
    public RenderType getRenderType(Object animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }
}
