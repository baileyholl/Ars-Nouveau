package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import javax.annotation.Nullable;

public class BasicTurretRenderer extends GeoBlockRenderer<BasicSpellTurretTile> {
    public static AnimatedGeoModel model = new GenericModel("basic_spell_turret");

    public BasicTurretRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        this(rendererDispatcherIn, model);
    }

    public BasicTurretRenderer(TileEntityRendererDispatcher rendererDispatcherIn, AnimatedGeoModel<BasicSpellTurretTile> modelProvider) {
        super(rendererDispatcherIn, modelProvider);
    }

    @Override
    public void render(GeoModel model, BasicSpellTurretTile animatable, float partialTicks, RenderType type, MatrixStack matrixStackIn, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStackIn.pushPose();
        Direction direction = animatable.getBlockState().getValue(BasicSpellTurret.FACING);
        if(direction == Direction.UP){
            matrixStackIn.translate(0, -0.5, -0.5);
        }else if(direction == Direction.DOWN){
            matrixStackIn.translate(0, -0.5, 0.5);
        }
        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);

        matrixStackIn.popPose();
    }

    public static GenericItemRenderer getISTER(){
        return new GenericItemRenderer(model);
    }
}
