package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BookwyrmRenderer extends TextureVariantRenderer<EntityBookwyrm> {

    public static ResourceLocation BLUE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/book_wyrm_blue.png");

    public BookwyrmRenderer(EntityRendererProvider.Context manager) {
        super(manager, new BookwyrmModel<>());
    }

    @Override
    public void render(EntityBookwyrm entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        stack.pushPose();
        stack.scale(0.5f, 0.5f, 0.5f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
        stack.popPose();
    }

}