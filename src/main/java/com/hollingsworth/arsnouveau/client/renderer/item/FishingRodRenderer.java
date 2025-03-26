package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.items.EnchantersFishingRod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.Color;

public class FishingRodRenderer extends GeoItemRenderer<EnchantersFishingRod> {
    public FishingRodRenderer() {
        super(new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(EnchantersFishingRod wand) {
                return ArsNouveau.prefix("geo/enchanters_rod.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(EnchantersFishingRod wand) {
                return ArsNouveau.proxy.getPlayer().fishing == null ? ArsNouveau.prefix("textures/item/enchanters_rod_stowed.png") : ArsNouveau.prefix("textures/item/enchanters_rod_cast.png");
            }

            @Override
            public ResourceLocation getAnimationResource(EnchantersFishingRod wand) {
                return ArsNouveau.prefix("animations/enchanters_fishing_rod.json");
            }
        });
    }

    @Override
    public void renderRecursively(PoseStack poseStack, EnchantersFishingRod animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        if (bone.getName().equals("blade")) {
            //NOTE: if the bone have a parent, the recursion will get here with the neutral color, making the color getter useless
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
        } else {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, Color.WHITE.argbInt());
        }
    }

    @Override
    public Color getRenderColor(EnchantersFishingRod animatable, float partialTick, int packedLight) {
        ParticleColor color = ParticleColor.defaultParticleColor();
        var caster = SpellCasterRegistry.from(currentItemStack);
        if (caster != null){
            color = caster.getColor();
        }
        return Color.ofRGBA(color.getRed(), color.getGreen(), color.getBlue(), 0.75f);
    }
}
