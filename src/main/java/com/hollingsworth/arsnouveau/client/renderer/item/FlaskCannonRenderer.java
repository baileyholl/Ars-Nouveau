package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.items.FlaskCannon;
import com.hollingsworth.arsnouveau.common.items.data.PotionLauncherData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;


public class FlaskCannonRenderer extends GeoItemRenderer<FlaskCannon> {


    public FlaskCannonRenderer(GeoModel<FlaskCannon> modelProvider) {
        super(modelProvider);
    }

    public FlaskCannonRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet, GeoModel<FlaskCannon> modelProvider) {
        super(dispatcher, modelSet, modelProvider);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, FlaskCannon animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int packedColor) {
        if(currentItemStack == null) {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, packedColor);
            return;
        }
        PotionLauncherData flask = currentItemStack.getOrDefault(DataComponentRegistry.POTION_LAUNCHER, new PotionLauncherData());
        int amountLeft = flask.amountLeft(Minecraft.getInstance().player);
        if(bone == null)
            return;
        if(bone.getName().equalsIgnoreCase("full")){
            bone.setHidden(amountLeft < 8);
        }else if(bone.getName().equalsIgnoreCase("75")){
            bone.setHidden(!(amountLeft == 7 || amountLeft == 6));
        }else if(bone.getName().equalsIgnoreCase("50")){
            bone.setHidden(!(amountLeft == 5 || amountLeft == 4));
        }else if(bone.getName().equalsIgnoreCase("25")){
            bone.setHidden(!(amountLeft == 3 || amountLeft == 2));
        }else if(bone.getName().equalsIgnoreCase("1")){
            bone.setHidden(amountLeft != 1);
        }

        if(bone.getName().equals("potion_levels") || (bone.getParent() != null && bone.getParent().getName().equals("potion_levels"))) {
            ParticleColor color = ParticleColor.fromInt(flask.renderData().getColor());
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color.getColor());
        }else{
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, packedColor);
        }
    }

    @Override
    public RenderType getRenderType(FlaskCannon animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
