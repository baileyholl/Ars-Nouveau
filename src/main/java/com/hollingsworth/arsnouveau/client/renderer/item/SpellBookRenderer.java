package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SpellBookRenderer extends GeoItemRenderer<SpellBook> {
    public SpellBookRenderer() {
        super(new SpellBookModel());
    }

// TODO: fix spellbook renderer

//    @Override
//    public void actuallyRender(PoseStack poseStack, SpellBook animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
//        if(this.model instanceof TransformAnimatedModel<SpellBook> transformed){
//            ResourceLocation modelLoc =  transformed.getModelResource((GeoAnimatable) animatable, transformType)
//            model = transformed.getModelResource((GeoAnimatable) animatable, transformType);
//        }
//        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
//    }

//    public void render(Item animatable, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn, ItemStack itemStack, ItemDisplayContext transformType) {
//        this.currentItemStack = itemStack;
//        GeoModel model = this.model;
//        if(model instanceof TransformAnimatedModel transformed){
//            model = getGeoModel(). transformed.getModelResource((GeoAnimatable) animatable, transformType);
//        }
//        GeoModel model = modelProvider instanceof TransformAnimatedModel transformAnimatedModel ? modelProvider.getModel(transformAnimatedModel.getModelResource((GeoAnimatable) animatable, transformType)) : modelProvider.getModel(modelProvider.getModelResource((SpellBook) animatable));
//        AnimationState<?> itemEvent = new AnimationState<>((GeoAnimatable) animatable, 0, 0, Minecraft.getInstance().getFrameTime(), false, Collections.singletonList(itemStack));
//        modelProvider.setCustomAnimations((SpellBook) animatable, this.getInstanceId((SpellBook) animatable), itemEvent);
//        stack.pushPose();
//        stack.translate(0, 0.01f, 0);
//        stack.translate(0.5, 0.5, 0.5);
//        RenderSystem.setShaderTexture(0, getTextureLocation((SpellBook) animatable));
//        Color renderColor = getRenderColor((SpellBook) animatable, 0, stack, bufferIn, null, packedLightIn);
//        RenderType renderType = getRenderType((SpellBook) animatable, 0, stack, bufferIn, null, packedLightIn, getTextureLocation((SpellBook) animatable));
//        render(model, (SpellBook) animatable, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY, renderColor.getRed() / 255f, renderColor.getGreen() / 255f, renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
//        stack.popPose();
//    }


    @Override
    public ResourceLocation getTextureLocation(SpellBook o) {
        String base = "textures/item/spellbook_";
        String color = !currentItemStack.hasTag() || !currentItemStack.getTag().contains("color") ? "purple" : DyeColor.byId(currentItemStack.getOrCreateTag().getInt("color")).getName();
        return new ResourceLocation(ArsNouveau.MODID, base + color + ".png");
    }
}
