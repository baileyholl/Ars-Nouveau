package com.hollingsworth.arsnouveau.client.renderer.entity.familiar;

import com.hollingsworth.arsnouveau.api.client.CosmeticRenderUtil;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.client.renderer.entity.GenericRenderer;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;


public class GenericFamiliarRenderer<T extends FamiliarEntity> extends GenericRenderer<T> {

    private T familiar;
    private MultiBufferSource buffer;

    public GenericFamiliarRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> modelProvider) {
        super(renderManager, modelProvider);
    }

    @Override
    public void renderEarly(T familiar, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        this.familiar = familiar;
        this.buffer = renderTypeBuffer;
        super.renderEarly(familiar, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
    }

    @Override
    public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {

        if (this.familiar.getCosmeticItem().getItem() instanceof ICosmeticItem cosmetic && cosmetic.getBone().equals(bone.getName())) {
            CosmeticRenderUtil.renderCosmetic(bone, stack, this.buffer, familiar, packedLightIn);
            bufferIn = buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(familiar)));
        }

        super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity.getTexture(entity) == null ? super.getTextureLocation(entity) : entity.getTexture(entity);
    }

}
