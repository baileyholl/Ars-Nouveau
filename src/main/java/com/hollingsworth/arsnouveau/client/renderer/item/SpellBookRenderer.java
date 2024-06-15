package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.Nullable;

public class SpellBookRenderer extends FixedGeoItemRenderer<SpellBook> {
    public GeoModel<SpellBook> closedModel;


    public SpellBookRenderer() {
        super(new SpellBookModel(SpellBookModel.OPEN));
        this.closedModel = new SpellBookModel(SpellBookModel.CLOSED);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, SpellBook animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        model.getBone("tier3").ifPresent(bone -> bone.setHidden(animatable.tier.value < 3));
        model.getBone("tier1").ifPresent(bone -> bone.setHidden(animatable.tier.value != 1));
        model.getBone("tier2").ifPresent(bone -> bone.setHidden(animatable.tier.value != 2));
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (this.useEntityGuiLighting) {
            Lighting.setupForEntityInInventory();
        } else {
            Lighting.setupForFlatItems();
        }

        MultiBufferSource.BufferSource var10000;
        if (bufferSource instanceof MultiBufferSource.BufferSource bufferSource2) {
            var10000 = bufferSource2;
        } else {
            var10000 = Minecraft.getInstance().renderBuffers().bufferSource();
        }
        MultiBufferSource.BufferSource defaultBufferSource = var10000;
        RenderType renderType = this.getRenderType(this.animatable, this.getTextureLocation(this.animatable), defaultBufferSource, Minecraft.getInstance().getFrameTime());
        VertexConsumer buffer = ItemRenderer.getFoilBufferDirect(bufferSource, renderType, true, this.currentItemStack != null && this.currentItemStack.hasFoil());

        poseStack.pushPose();
        this.defaultRenderGui(poseStack, this.animatable, defaultBufferSource, renderType, buffer, 0.0F, Minecraft.getInstance().getFrameTime(), packedLight, packedOverlay);
        defaultBufferSource.endBatch();
        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();
        poseStack.popPose();
    }

    @Override
    public void defaultRender(PoseStack poseStack, SpellBook animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
    }

    public void defaultRenderGui(PoseStack poseStack, SpellBook animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        Color renderColor = this.getRenderColor(animatable, partialTick, packedLight);
        float red = renderColor.getRedFloat();
        float green = renderColor.getGreenFloat();
        float blue = renderColor.getBlueFloat();
        float alpha = renderColor.getAlphaFloat();
        BakedGeoModel model = closedModel.getBakedModel(closedModel.getModelResource(animatable));
        if (renderType == null) {
            renderType = this.getRenderType(animatable, this.getTextureLocation(animatable), bufferSource, partialTick);
        }

        if (buffer == null) {
            buffer = bufferSource.getBuffer(renderType);
        }

        this.preRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        if (this.firePreRenderEvent(poseStack, model, bufferSource, partialTick, packedLight)) {
            this.preApplyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, (float)packedLight, packedLight, packedOverlay);
            this.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
            this.applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
            this.postRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
            this.firePostRenderEvent(poseStack, model, bufferSource, partialTick, packedLight);
        }

        poseStack.popPose();
        this.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }


        @Override
    public ResourceLocation getTextureLocation(SpellBook o) {
        String base = "textures/item/spellbook_";
        String color = !currentItemStack.hasTag() || !currentItemStack.getTag().contains("color") ? "purple" : DyeColor.byId(currentItemStack.getOrCreateTag().getInt("color")).getName();
        return new ResourceLocation(ArsNouveau.MODID, base + color + ".png");
    }

    @Override
    public RenderType getRenderType(SpellBook animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
