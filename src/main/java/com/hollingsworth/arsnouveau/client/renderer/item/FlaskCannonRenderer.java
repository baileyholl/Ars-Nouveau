package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.items.FlaskCannon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class FlaskCannonRenderer extends GeoItemRenderer<FlaskCannon> {


    public FlaskCannonRenderer(AnimatedGeoModel<FlaskCannon> modelProvider) {
        super(modelProvider);
    }

    public FlaskCannonRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet, AnimatedGeoModel<FlaskCannon> modelProvider) {
        super(dispatcher, modelSet, modelProvider);
    }

    @Override
    public void renderEarly(FlaskCannon animatable, PoseStack stackIn, float partialTicks, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {

        super.renderEarly(animatable, stackIn, partialTicks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);

    }

    @Override
    public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if(currentItemStack == null){
            super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            return;
        }
        FlaskCannon.PotionLauncherData flask = new FlaskCannon.PotionLauncherData(currentItemStack);
        if(bone == null)
            return;

        if(bone.getName().equals("full")){
            bone.setHidden(flask.amountLeft < 8);
        }else if(bone.getName().equals("75")){
            bone.setHidden(!(flask.amountLeft == 7 || flask.amountLeft == 6));
        }else if(bone.getName().equals("50")){
            bone.setHidden(!(flask.amountLeft == 5 || flask.amountLeft == 4));
        }else if(bone.getName().equals("25")){
            bone.setHidden(!(flask.amountLeft == 3 || flask.amountLeft == 2));
        }else if(bone.getName().equals("1")){
            bone.setHidden(flask.amountLeft != 1);
        }

        if(bone.getName().equals("potion_levels") || (bone.getParent() != null && bone.getParent().getName().equals("potion_levels"))){
            ParticleColor color = ParticleColor.fromInt(PotionUtils.getColor(flask.getLastDataForRender().asPotionStack()));
            super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, color.getRed(), color.getGreen(), color.getBlue(), 0.1f);
        }else {
            super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }

    @Override
    public RenderType getRenderType(FlaskCannon animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityCutoutNoCull(textureLocation);
    }
}
