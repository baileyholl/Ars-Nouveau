package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.items.ScryCaster;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.Color;

public class ScryCasterRenderer extends FixedGeoItemRenderer<ScryCaster>{
    public ScryCasterRenderer() {
        super(new GeoModel<ScryCaster>() {
            @Override
            public ResourceLocation getModelResource(ScryCaster wand) {
                return ArsNouveau.prefix( "geo/enchanters_eye.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(ScryCaster wand) {
                return ArsNouveau.prefix( "textures/item/enchanters_eye.png");
            }

            @Override
            public ResourceLocation getAnimationResource(ScryCaster wand) {
                return ArsNouveau.prefix( "animations/enchanters_eye.json");
            }
        });
    }

    @Override
    public void renderRecursively(PoseStack poseStack, ScryCaster animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int packedColor) {
        if (bone.getName().equals("eye")) {
            //NOTE: if the bone have a parent, the recursion will get here with the neutral color, making the color getter useless
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, packedColor);
        } else {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, Color.WHITE.argbInt());
        }
    }

    @Override
    public Color getRenderColor(ScryCaster animatable, float partialTick, int packedLight) {
        ParticleColor color = ParticleColor.defaultParticleColor();
        var caster = SpellCasterRegistry.from(currentItemStack);
        if (caster != null){
            color = caster.getColor();
        }
        return Color.ofRGBA(color.getRed(), color.getGreen(), color.getBlue(), 0.85f).brighter(1.2f);
    }

    @Override
    public RenderType getRenderType(ScryCaster animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
