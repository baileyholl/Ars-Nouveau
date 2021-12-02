package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningCrystalTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class SummoningCrystalRenderer extends GeoBlockRenderer<SummoningCrystalTile> {

    public SummoningCrystalRenderer(BlockEntityRenderDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn, new SummoningCrystalModel());
    }

    @Override
    public RenderType getRenderType(SummoningCrystalTile animatable, float partialTicks, PoseStack stack, MultiBufferSource renderTypeBuffer,VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }

    public static GenericItemRenderer getISTER(){
        return new GenericItemRenderer(new SummoningCrystalModel());
    }
}