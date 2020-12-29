package com.hollingsworth.arsnouveau.client.renderer.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class FixedGeoItemRenderer<T extends Item & IAnimatable> extends GeoItemRenderer {
    public FixedGeoItemRenderer(AnimatedGeoModel modelProvider) {
        super(modelProvider);
    }

    @Override
    public void func_239207_a_(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack stack, IRenderTypeBuffer bufferIn, int combinedLightIn, int p_239207_6_) {
        if(transformType == ItemCameraTransforms.TransformType.GUI){
            RenderSystem.pushMatrix();
            IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
            RenderHelper.setupGuiFlatDiffuseLighting();
            super.func_239207_a_(itemStack, transformType, stack, bufferIn, 15728880, p_239207_6_);
            irendertypebuffer$impl.finish();
            RenderSystem.enableDepthTest();
            RenderHelper.setupGui3DDiffuseLighting();
            RenderSystem.popMatrix();
        }else {
            super.func_239207_a_(itemStack, transformType, stack, bufferIn, combinedLightIn, p_239207_6_);
        }
    }
}