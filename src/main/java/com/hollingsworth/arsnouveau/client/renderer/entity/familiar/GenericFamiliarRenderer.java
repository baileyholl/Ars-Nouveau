package com.hollingsworth.arsnouveau.client.renderer.entity.familiar;

import com.hollingsworth.arsnouveau.api.client.CosmeticRenderUtil;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarEntity;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GenericFamiliarRenderer<T extends FamiliarEntity> extends GeoEntityRenderer<T> {

    public GenericFamiliarRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
    }

    public static MultiBufferSource.BufferSource cosmeticBuffer = MultiBufferSource.immediate(new ByteBufferBuilder(1536));

    @Override
    public void renderRecursively(PoseStack stack, T familiar, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        if (familiar.getCosmeticItem().getItem() instanceof ICosmeticItem cosmetic && cosmetic.getBone(familiar).equals(bone.getName())) {
            CosmeticRenderUtil.renderCosmetic(bone, stack, cosmeticBuffer, familiar, packedLight);
            cosmeticBuffer.endBatch();
        }
        super.renderRecursively(stack, familiar, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(T entity) {
        return entity.getTexture() == null ? super.getTextureLocation(entity) : entity.getTexture();
    }

}
