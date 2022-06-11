package com.hollingsworth.arsnouveau.client.renderer.entity.familiar;

import com.hollingsworth.arsnouveau.client.renderer.entity.BookwyrmModel;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarBookwyrm;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class FamiliarBookwyrmRenderer<T extends FamiliarBookwyrm> extends GenericFamiliarRenderer<T>{
    public FamiliarBookwyrmRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BookwyrmModel<>());
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        stack.pushPose();
        stack.scale(0.5f, 0.5f, 0.5f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
        stack.popPose();
    }




}
