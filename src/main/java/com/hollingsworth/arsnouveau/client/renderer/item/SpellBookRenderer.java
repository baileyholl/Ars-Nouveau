package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

import java.awt.*;
import java.util.Collections;

public class SpellBookRenderer extends GeoItemRenderer<SpellBook> {
    public SpellBookRenderer(){
        super(new SpellBookModel());
    }

//    @Override
//    public void render(Item animatable, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn, ItemStack itemStack, ItemCameraTransforms.TransformType transformType) {
//        super.render(animatable, stack, bufferIn, packedLightIn, itemStack, transformType);
//    }

    @Override
    public void func_239207_a_(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack stack, IRenderTypeBuffer bufferIn, int combinedLightIn, int p_239207_6_) {
        if(transformType == ItemCameraTransforms.TransformType.GUI){
            RenderSystem.pushMatrix();
            IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
            RenderHelper.setupGuiFlatDiffuseLighting();
            render((SpellBook) itemStack.getItem(), stack, bufferIn, 15728880, itemStack, transformType);
            irendertypebuffer$impl.finish();
            RenderSystem.enableDepthTest();
            RenderHelper.setupGui3DDiffuseLighting();
            RenderSystem.popMatrix();
        }else {
            render(itemStack.getItem(), stack, bufferIn, combinedLightIn, itemStack, transformType);
        }
    }

    public void render(Item animatable, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn, ItemStack itemStack, ItemCameraTransforms.TransformType transformType) {
//        super.render(animatable, stack, bufferIn, packedLightIn, itemStack, transformType);
        this.currentItemStack = itemStack;
        GeoModel model = modelProvider instanceof TransformAnimatedModel ? modelProvider.getModel(((TransformAnimatedModel) modelProvider).getModelLocation((IAnimatable) animatable, transformType)) : modelProvider.getModel(modelProvider.getModelLocation((SpellBook) animatable));
        AnimationEvent itemEvent = new AnimationEvent((IAnimatable) animatable, 0, 0, Minecraft.getInstance().getRenderPartialTicks(), false, Collections.singletonList(itemStack));
        modelProvider.setLivingAnimations((SpellBook) animatable, this.getUniqueID((SpellBook) animatable), itemEvent);
        stack.push();
        stack.translate(0, 0.01f, 0);
        stack.translate(0.5, 0.5, 0.5);
        Minecraft.getInstance().textureManager.bindTexture(getTextureLocation((SpellBook) animatable));
//        System.out.println(getTextureLocation((SpellBook) animatable));
        Color renderColor = getRenderColor((SpellBook) animatable, 0, stack, bufferIn, null, packedLightIn);
        RenderType renderType = getRenderType((SpellBook) animatable, 0, stack, bufferIn, null, packedLightIn, getTextureLocation((SpellBook) animatable));
        render(model, (SpellBook) animatable, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
        stack.pop();
    }

    @Override
    public ResourceLocation getTextureLocation(SpellBook o) {
        String base = "textures/items/spellbook_";
        String color = !currentItemStack.hasTag() || !currentItemStack.getTag().contains("color") ? "purple" : DyeColor.byId(currentItemStack.getOrCreateTag().getInt("color")).getTranslationKey();

       // new ResourceLocation(ArsNouveau.MODID, "textures/items/spellbook_purple.png");
       //System.out.println(color);
        return new ResourceLocation(ArsNouveau.MODID, base + color +".png");
    }
}