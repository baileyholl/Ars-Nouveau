package com.hollingsworth.arsnouveau.client.renderer.entity;


import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StyledSpellRender extends GeoEntityRenderer<EntityProjectileSpell> {

    public StyledSpellRender(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new StyledProjectileModel());
    }

    @Override
    public void render(EntityProjectileSpell entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

    }

    @Override
    public @Nullable RenderType getRenderType(EntityProjectileSpell animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(texture);
    }
}