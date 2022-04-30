package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.tile.ScryersEyeTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class ScryerEyeRenderer extends GeoBlockRenderer<ScryersEyeTile> {
    ScryersEyeModel model;
    public ScryerEyeRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, ScryersEyeModel model) {
        super(rendererDispatcherIn, model);
        this.model = model;
    }

    @Override
    public void render(GeoModel model, ScryersEyeTile pBlockEntity, float pPartialTick, RenderType type, PoseStack pPoseStack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        pPoseStack.pushPose();
//        float f1;
//        for(f1 = pBlockEntity.rot - pBlockEntity.oRot; f1 >= (float)Math.PI; f1 -= ((float)Math.PI * 2F)) {
//        }
//
//        while(f1 < -(float)Math.PI) {
//            f1 += ((float)Math.PI * 2F);
//        }
//
//        float f2 = pBlockEntity.oRot + f1 * pPartialTick;
//        pPoseStack.mulPose(Vector3f.YP.rotation(-f2));
//        pPoseStack.mulPose(Vector3f.ZP.rotationDegrees(80.0F));
//        float f3 = Mth.lerp(pPartialTick, pBlockEntity.oFlip, pBlockEntity.flip);
//        float f4 = Mth.frac(f3 + 0.25F) * 1.6F - 0.3F;
//        float f5 = Mth.frac(f3 + 0.75F) * 1.6F - 0.3F;
//        float f6 = Mth.lerp(pPartialTick, pBlockEntity.oOpen, pBlockEntity.open);
//        pPoseStack.translate(0, (Mth.sin((float) ((ClientInfo.ticksInGame + pPartialTick)/ 20.0d)) + 1)/18.0, 0);

        super.render(model, pBlockEntity, pPartialTick, type, pPoseStack, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        pPoseStack.popPose();


//        this.model.setupAnim(f, Mth.clamp(f4, 0.0F, 1.0F), Mth.clamp(f5, 0.0F, 1.0F), f6);
//        VertexConsumer vertexconsumer = BOOK_LOCATION.buffer(pBufferSource, RenderType::entitySolid);
//        this.bookModel.render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
//        pPoseStack.popPose();
    }

    @Override
    public void render(BlockEntity tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        super.render(tile, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    @Override
    public void render(ScryersEyeTile tile, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(tile, partialTicks, stack, bufferIn, packedLightIn);

    }

    public void setupAnim(float ticks, float p_102294_, float p_102295_, float p_102296_) {
//        float f = (Mth.sin(ticks * 0.02F) * 0.1F + 1.25F) * p_102296_;
//        this.leftLid.yRot = (float)Math.PI + f;
//        this.rightLid.yRot = -f;
//        this.leftPages.yRot = f;
//        this.rightPages.yRot = -f;
//        this.flipPage1.yRot = f - f * 2.0F * p_102294_;
//        this.flipPage2.yRot = f - f * 2.0F * p_102295_;
//        this.leftPages.x = Mth.sin(f);
//        this.rightPages.x = Mth.sin(f);
//        this.flipPage1.x = Mth.sin(f);
//        this.flipPage2.x = Mth.sin(f);
    }
}
