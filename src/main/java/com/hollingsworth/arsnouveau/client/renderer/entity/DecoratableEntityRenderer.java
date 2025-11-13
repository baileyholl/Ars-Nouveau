package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.api.client.CosmeticRenderUtil;
import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DecoratableEntityRenderer<T extends LivingEntity & GeoAnimatable & IDecoratable> extends GeoEntityRenderer<T> {

    public static MultiBufferSource.BufferSource cosmeticBuffer = MultiBufferSource.immediate(new ByteBufferBuilder(1536));

    public DecoratableEntityRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        if (animatable.getCosmeticItem().getItem() instanceof ICosmeticItem cosmetic && cosmetic.getBone(animatable).equals(bone.getName())) {
            CosmeticRenderUtil.renderCosmetic(bone, poseStack, cosmeticBuffer, animatable, packedLight);
            cosmeticBuffer.endBatch();
        }
    }
}
