package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.ScryCaster;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ScryCasterRenderer extends GeoItemRenderer<ScryCaster> {
    public ScryCasterRenderer() {
        super(new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(ScryCaster wand) {
                return ArsNouveau.prefix("geo/enchanters_eye.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(ScryCaster wand) {
                return ArsNouveau.prefix("textures/item/enchanters_eye.png");
            }

            @Override
            public ResourceLocation getAnimationResource(ScryCaster wand) {
                return ArsNouveau.prefix("animations/enchanters_eye.json");
            }
        });
    }

    @Override
    public void renderRecursively(PoseStack poseStack, ScryCaster animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int packedColor) {
        if (bone.getName().equals("eye")) {
            DyeColor color1 = getCurrentItemStack().getOrDefault(DataComponents.BASE_COLOR, DyeColor.PURPLE);
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, FastColor.ABGR32.color(200, color1.getTextColor()));
        } else {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, packedColor);
        }
    }

    @Override
    public RenderType getRenderType(ScryCaster animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
