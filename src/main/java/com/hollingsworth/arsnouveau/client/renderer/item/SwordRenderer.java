package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.items.EnchantersSword;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.Color;
import software.bernie.geckolib.model.GeoModel;

public class SwordRenderer extends FixedGeoItemRenderer<EnchantersSword> {
    public SwordRenderer() {
        super(new GeoModel<EnchantersSword>() {
            @Override
            public ResourceLocation getModelResource(EnchantersSword wand) {
                return ArsNouveau.prefix( "geo/sword.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(EnchantersSword wand) {
                return ArsNouveau.prefix( "textures/item/enchanters_sword.png");
            }

            @Override
            public ResourceLocation getAnimationResource(EnchantersSword wand) {
                return ArsNouveau.prefix( "animations/sword.json");
            }
        });
    }
    @Override
    public void renderRecursively(PoseStack poseStack, EnchantersSword animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("blade")) {
            //NOTE: if the bone have a parent, the recursion will get here with the neutral color, making the color getter useless
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        } else {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, Color.WHITE.getRed() / 255f, Color.WHITE.getGreen() / 255f, Color.WHITE.getBlue() / 255f, Color.WHITE.getAlpha() / 255f);
        }
    }

    @Override
    public Color getRenderColor(EnchantersSword animatable, float partialTick, int packedLight) {
        ParticleColor color = ParticleColor.defaultParticleColor();
        if (currentItemStack.hasTag())
            if (currentItemStack.getOrCreateTag().contains("ars_nouveau:caster")) {
                color = animatable.getSpellCaster(currentItemStack).getColor();
            }
        return Color.ofRGBA(color.getRed(), color.getGreen(), color.getBlue(), 0.75f);
    }

    @Override
    public RenderType getRenderType(EnchantersSword animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
