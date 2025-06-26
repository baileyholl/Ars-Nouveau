package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.EnchantersSword;
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

public class SwordRenderer extends GeoItemRenderer<EnchantersSword> {
    public SwordRenderer() {
        super(new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(EnchantersSword wand) {
                return ArsNouveau.prefix("geo/sword.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(EnchantersSword wand) {
                return ArsNouveau.prefix("textures/item/enchanters_sword.png");
            }

            @Override
            public ResourceLocation getAnimationResource(EnchantersSword wand) {
                return ArsNouveau.prefix("animations/sword.json");
            }
        });
    }

    @Override
    public void renderRecursively(PoseStack poseStack, EnchantersSword animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        if (bone.getName().equals("blade")) {
            DyeColor color1 = getCurrentItemStack().getOrDefault(DataComponents.BASE_COLOR, DyeColor.PURPLE);
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, FastColor.ABGR32.color(200, color1.getTextColor()));
        } else {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
        }
    }

    @Override
    public RenderType getRenderType(EnchantersSword animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
