package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningCrystalTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class SummoningCrystalRenderer extends GeoBlockRenderer<SummoningCrystalTile> {

    public SummoningCrystalRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn, new SummoningCrystalModel());
    }

    @Override
    public RenderType getRenderType(SummoningCrystalTile animatable, float partialTicks, MatrixStack stack, IRenderTypeBuffer renderTypeBuffer,IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }

    public static GenericItemRenderer getISTER(){
        return new GenericItemRenderer(new SummoningCrystalModel());
    }
}