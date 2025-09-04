package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.EnchantersFishingRod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeColor;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

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
            DyeColor color1 = getCurrentItemStack().getOrDefault(DataComponents.BASE_COLOR, DyeColor.PURPLE);
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, FastColor.ABGR32.color(200, color1.getTextColor()));
        } else {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
        }
    }
}
