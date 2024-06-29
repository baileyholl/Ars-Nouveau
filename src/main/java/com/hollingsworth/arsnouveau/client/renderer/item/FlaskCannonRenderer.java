package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.items.FlaskCannon;
import com.hollingsworth.arsnouveau.common.items.data.PotionLauncherData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.PotionContents;
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
        PotionLauncherData flask = new PotionLauncherData(currentItemStack);
        if(bone == null)
            return;
        if(bone.getName().equalsIgnoreCase("full")){
            bone.setHidden(flask.amountLeft < 8);
        }else if(bone.getName().equalsIgnoreCase("75")){
            bone.setHidden(!(flask.amountLeft == 7 || flask.amountLeft == 6));
        }else if(bone.getName().equalsIgnoreCase("50")){
            bone.setHidden(!(flask.amountLeft == 5 || flask.amountLeft == 4));
        }else if(bone.getName().equalsIgnoreCase("25")){
            bone.setHidden(!(flask.amountLeft == 3 || flask.amountLeft == 2));
        }else if(bone.getName().equalsIgnoreCase("1")){
            bone.setHidden(flask.amountLeft != 1);
        }

        if(bone.getName().equals("potion_levels") || (bone.getParent() != null && bone.getParent().getName().equals("potion_levels"))) {
            ParticleColor color = ParticleColor.fromInt(PotionContents.getColor(flask.getLastDataForRender().asPotionStack()));
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
