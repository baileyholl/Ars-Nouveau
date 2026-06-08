package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ArcanoBoss;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ArcanoBossRenderer extends MobRenderer<ArcanoBoss, ArcanoBossModel> {
    private static final ResourceLocation SKIN = ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "textures/entity/boss_texture.png");

    public ArcanoBossRenderer(EntityRendererProvider.Context context) {
        super(context, new ArcanoBossModel(context.bakeLayer(ArcanoBossModel.LAYER_LOCATION)), 0.7F);
    }

    @Override
    public void render(ArcanoBoss rootminEntity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        super.render(rootminEntity, entityYaw, partialTicks, stack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ArcanoBoss rootminEntity) {
        return SKIN;
    }
}