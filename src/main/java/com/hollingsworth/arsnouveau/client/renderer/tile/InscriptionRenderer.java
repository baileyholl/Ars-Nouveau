package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.common.block.InscriptionTable;
import com.hollingsworth.arsnouveau.common.block.tile.InscriptionTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BedPart;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import javax.annotation.Nullable;

public class InscriptionRenderer extends GeoBlockRenderer<InscriptionTile> {
    public static AnimatedGeoModel model = new GenericModel("inscription_table");


    public InscriptionRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, model);
    }

    @Override
    public void render(InscriptionTile tile, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        try{
//            if(tile.getLevel().getBlockState(tile.getBlockPos()).getBlock() != BlockRegistry.INSCRIPTION_TABLE)
//                return;
            if(tile.getLevel().getBlockState(tile.getBlockPos()).getValue(InscriptionTable.PART) != BedPart.HEAD)
                return;
            Direction direction = tile.getLevel().getBlockState(tile.getBlockPos()).getValue(InscriptionTable.FACING);
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
        }catch (Throwable t){
            t.printStackTrace();
            // why must people change the rendering order of tesrs
        }
    }
    public static GenericItemRenderer getISTER(){
        return new GenericItemRenderer(model);
    }

    @Override
    public RenderType getRenderType(InscriptionTile animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }

    @Override
    public boolean shouldRenderOffScreen(BlockEntity p_188185_1_) {
        return false;
    }
}
