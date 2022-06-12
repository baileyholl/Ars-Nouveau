package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

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
    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack stack, MultiBufferSource bufferIn, int combinedLightIn, int p_239207_6_) {
        if(transformType == ItemTransforms.TransformType.GUI){
            stack.pushPose();
            MultiBufferSource.BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
            Lighting.setupForFlatItems();
            render(itemStack.getItem(), stack, bufferIn, 15728880, itemStack, transformType);
            irendertypebuffer$impl.endBatch();
            RenderSystem.enableDepthTest();
            Lighting.setupFor3DItems();
            stack.popPose();
        }else {
            render(itemStack.getItem(), stack, bufferIn, combinedLightIn, itemStack, transformType);
        }
    }

    public void render(Item animatable, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn, ItemStack itemStack, ItemTransforms.TransformType transformType) {
//        super.render(animatable, stack, bufferIn, packedLightIn, itemStack, transformType);
        this.currentItemStack = itemStack;
        GeoModel model = modelProvider instanceof TransformAnimatedModel ? modelProvider.getModel(((TransformAnimatedModel) modelProvider).getModelResource((IAnimatable) animatable, transformType)) : modelProvider.getModel(modelProvider.getModelResource((SpellBook) animatable));
        AnimationEvent itemEvent = new AnimationEvent((IAnimatable) animatable, 0, 0, Minecraft.getInstance().getFrameTime(), false, Collections.singletonList(itemStack));
        modelProvider.setLivingAnimations((SpellBook) animatable, this.getUniqueID((SpellBook) animatable), itemEvent);
        stack.pushPose();
        stack.translate(0, 0.01f, 0);
        stack.translate(0.5, 0.5, 0.5);
//        System.out.println(getTextureResource((SpellBook) animatable));
        RenderSystem.setShaderTexture(0, getTextureResource((SpellBook) animatable));
        Color renderColor = getRenderColor((SpellBook) animatable, 0, stack, bufferIn, null, packedLightIn);
        RenderType renderType = getRenderType((SpellBook) animatable, 0, stack, bufferIn, null, packedLightIn, getTextureResource((SpellBook) animatable));
        render(model, (SpellBook) animatable, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
        stack.popPose();
    }

    @Override
    public ResourceLocation getTextureResource(SpellBook o) {
        String base = "textures/items/spellbook_";
        String color = !currentItemStack.hasTag() || !currentItemStack.getTag().contains("color") ? "purple" : DyeColor.byId(currentItemStack.getOrCreateTag().getInt("color")).getName();

       // new ResourceLocation(ArsNouveau.MODID, "textures/items/spellbook_purple.png");
       //System.out.println(color);
        return new ResourceLocation(ArsNouveau.MODID, base + color +".png");
    }
}
