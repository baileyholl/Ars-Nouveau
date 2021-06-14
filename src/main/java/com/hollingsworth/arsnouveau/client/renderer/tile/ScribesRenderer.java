package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import javax.annotation.Nullable;

public class ScribesRenderer extends GeoBlockRenderer<ScribesTile> {

    public ScribesRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn, new ScribesModel());
    }

    @Override
    public void render(ScribesTile tile, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if(tile.getLevel().getBlockState(tile.getBlockPos()).getValue(ScribesBlock.PART) != BedPart.HEAD)
            return;
        Direction direction = tile.getLevel().getBlockState(tile.getBlockPos()).getValue(ScribesBlock.FACING);
        stack.pushPose();

        if(direction == Direction.NORTH){
            stack.mulPose(Vector3f.YP.rotationDegrees(-90));
            stack.translate(1, 0, -1);
        }

        if(direction == Direction.SOUTH){
            stack.mulPose(Vector3f.YP.rotationDegrees(90));
            stack.translate(-1, 0, 0);
        }

        if(direction == Direction.WEST){
            stack.mulPose(Vector3f.YP.rotationDegrees(90));
            stack.translate(-1, 0, 0);

        }

        if(direction == Direction.EAST){
            stack.mulPose(Vector3f.YP.rotationDegrees(-90));
            stack.translate(0, 0, 0);

        }
        super.render(tile, partialTicks, stack, bufferIn, packedLightIn);
        stack.popPose();
    }

    public static GenericItemRenderer getISTER(){
        return new GenericItemRenderer(new ScribesModel());
    }

    @Override
    public RenderType getRenderType(ScribesTile animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }
}
