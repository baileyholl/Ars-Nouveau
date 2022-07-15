package com.hollingsworth.arsnouveau.client.renderer.item;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

import java.util.Collections;

public class FixedGeoItemRenderer<T extends Item & IAnimatable> extends GeoItemRenderer {
    public FixedGeoItemRenderer(AnimatedGeoModel modelProvider) {
        super(modelProvider);
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack stack, MultiBufferSource bufferIn, int combinedLightIn, int p_239207_6_) {
        if (transformType == ItemTransforms.TransformType.GUI) {

            stack.pushPose();
            MultiBufferSource.BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
            Lighting.setupForFlatItems();
            render(itemStack.getItem(), stack, bufferIn, 15728880, itemStack, transformType);
            irendertypebuffer$impl.endBatch();
            RenderSystem.enableDepthTest();
            Lighting.setupFor3DItems();
            stack.popPose();
        } else {
            render(itemStack.getItem(), stack, bufferIn, combinedLightIn, itemStack, transformType);
        }
    }

    public void render(Item animatable, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn, ItemStack itemStack, ItemTransforms.TransformType transformType) {
//        super.render(animatable, stack, bufferIn, packedLightIn, itemStack, transformType);
        this.currentItemStack = itemStack;
        GeoModel model = modelProvider instanceof TransformAnimatedModel ? modelProvider.getModel(((TransformAnimatedModel) modelProvider).getModelResource((IAnimatable) animatable, transformType)) : modelProvider.getModel(modelProvider.getModelResource(animatable));
        AnimationEvent itemEvent = new AnimationEvent((IAnimatable) animatable, 0, 0, Minecraft.getInstance().getFrameTime(), false, Collections.singletonList(itemStack));
        if (modelProvider == null)
            return;
        modelProvider.setLivingAnimations((IAnimatable) animatable, this.getUniqueID(animatable), itemEvent);
        stack.pushPose();
        stack.translate(0, 0.01f, 0);
        stack.translate(0.5, 0.5, 0.5);
        RenderSystem.setShaderTexture(0, getTextureLocation(animatable));
        Color renderColor = getRenderColor(animatable, 0, stack, bufferIn, null, packedLightIn);
        RenderType renderType = getRenderType(animatable, 0, stack, bufferIn, null, packedLightIn, getTextureLocation(animatable));
        render(model, animatable, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
        stack.popPose();
    }
}