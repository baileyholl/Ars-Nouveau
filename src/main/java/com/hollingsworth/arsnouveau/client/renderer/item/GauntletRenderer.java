package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.items.EnchantersGauntlet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.Color;

public class GauntletRenderer extends GeoItemRenderer<EnchantersGauntlet> {
    public GauntletRenderer() {
        super(new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(EnchantersGauntlet wand) {
                return ArsNouveau.prefix("geo/enchanters_gauntlet.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(EnchantersGauntlet wand) {
                return ArsNouveau.prefix("textures/item/enchanters_gauntlet.png");
            }

            @Override
            public ResourceLocation getAnimationResource(EnchantersGauntlet wand) {
                return ArsNouveau.prefix("animations/enchanters_gauntlet.json");
            }
        });
    }

    @Override
    public void renderRecursively(PoseStack poseStack, EnchantersGauntlet animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        if (bone.getName().equals("blade")) {
            //NOTE: if the bone have a parent, the recursion will get here with the neutral color, making the color getter useless
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
        } else {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, Color.WHITE.argbInt());
        }
    }

    @Override
    public Color getRenderColor(EnchantersGauntlet animatable, float partialTick, int packedLight) {
        ParticleColor color = ParticleColor.defaultParticleColor();
        var caster = SpellCasterRegistry.from(currentItemStack);
        if (caster != null) {
            var timeline = caster.getSpell().particleTimeline().get(ParticleTimelineRegistry.TOUCH_TIMELINE.get());
            color = timeline.onResolvingEffect.particleOptions().colorProp().color();
        }
        return Color.ofRGBA(color.getRed(), color.getGreen(), color.getBlue(), 0.75f);
    }
}
