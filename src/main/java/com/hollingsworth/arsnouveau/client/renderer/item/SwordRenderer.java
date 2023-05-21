package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.items.EnchantersSword;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SwordRenderer extends FixedGeoItemRenderer<EnchantersSword> {
    public SwordRenderer() {
        super(new AnimatedGeoModel<EnchantersSword>() {
            @Override
            public ResourceLocation getModelResource(EnchantersSword wand) {
                return new ResourceLocation(ArsNouveau.MODID, "geo/sword.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(EnchantersSword wand) {
                return new ResourceLocation(ArsNouveau.MODID, "textures/items/enchanters_sword.png");
            }

            @Override
            public ResourceLocation getAnimationResource(EnchantersSword wand) {
                return new ResourceLocation(ArsNouveau.MODID, "animations/sword.json");
            }
        });
    }

    @Override
    public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        //we override the color getter for a specific bone, this means the other ones need to use the neutral color
        if (bone.getName().equals("blade")) {
            //NOTE: if the bone have a parent, the recursion will get here with the neutral color, making the color getter useless
            super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        } else {
            super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, Color.WHITE.getRed() / 255f, Color.WHITE.getGreen() / 255f, Color.WHITE.getBlue() / 255f, Color.WHITE.getAlpha() / 255f);
        }
    }

    @Override
    public Color getRenderColor(Object animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight) {
        ParticleColor color = ParticleColor.defaultParticleColor();;
        if (currentItemStack.hasTag())
            if (currentItemStack.getOrCreateTag().contains("ars_nouveau:caster")) {
                color = ((EnchantersSword) animatable).getSpellCaster(currentItemStack).getColor();
            }
        return Color.ofRGB(color.toWrapper().r, color.toWrapper().g, color.toWrapper().b);
    }

    @Override
    public RenderType getRenderType(Object animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }
}
