package com.hollingsworth.arsnouveau.client.renderer.entity;


import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ColorProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.ModelProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.particle.timelines.ProjectileTimeline;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StyledSpellRender extends GeoEntityRenderer<EntityProjectileSpell> {

    ModelProperty modelProp;
    public StyledSpellRender(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new StyledProjectileModel());
    }

    @Override
    public void render(EntityProjectileSpell entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        ProjectileTimeline timeline = entity.resolver().spell.particleTimeline().get(ParticleTimelineRegistry.PROJECTILE_TIMELINE.get());
        ModelProperty modelProperty = timeline.trailEffect.particleOptions().map.get(ParticlePropertyRegistry.MODEL_PROPERTY.get());
        if(modelProperty == null || modelProperty.selectedResource == ModelProperty.NONE){
            return;
        }
        modelProp = modelProperty;
        packedLight = LightTexture.FULL_BRIGHT;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, EntityProjectileSpell animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        poseStack.pushPose();
        if(modelProp.selectedResource == ModelProperty.CUBE_BODY){
            packedLight = LightTexture.FULL_BRIGHT;
            poseStack.translate(0, -0.25, 0);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.yRot) - 90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
        }

        if(modelProp.selectedResource.supportsColor()){
            colour = modelProp.subPropMap.getOrDefault(ParticlePropertyRegistry.COLOR_PROPERTY.get(), new ColorProperty(new PropMap())).particleColor.getColor();
        }

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        poseStack.popPose();
    }

    @Override
    public @Nullable RenderType getRenderType(EntityProjectileSpell animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(texture);
    }

    @Override
    public GeoModel<EntityProjectileSpell> getGeoModel() {
        return super.getGeoModel();
    }
}