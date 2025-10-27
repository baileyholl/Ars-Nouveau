package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.armor.AnimatedMagicArmor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.Optional;

public class ArmorRenderer extends GeoArmorRenderer<AnimatedMagicArmor> {

    public ArmorRenderer(GeoModel<AnimatedMagicArmor> modelProvider) {
        super(modelProvider);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, AnimatedMagicArmor animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        if (bone.getName().equalsIgnoreCase("armorRightArmSlim") || bone.getName().equalsIgnoreCase("armorLeftArmSlim")) {
            bone.setHidden(true);
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, AnimatedMagicArmor animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        Optional<GeoBone> slimRight = model.getBone("armorRightArmSlim");
        Optional<GeoBone> slimLeft = model.getBone("armorLeftArmSlim");
        slimRight.ifPresent(geoBone -> geoBone.setHidden(true));
        slimLeft.ifPresent(geoBone -> geoBone.setHidden(true));
        model.getBone("armorRightArmSlim").ifPresent(geoBone -> geoBone.setHidden(true));
        model.getBone("armorLeftArmSlim").ifPresent(geoBone -> geoBone.setHidden(true));
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
    }


    @Override
    public ResourceLocation getTextureLocation(AnimatedMagicArmor instance) {
        if (instance != null && model instanceof GenericModel<AnimatedMagicArmor> genericModel) {
            DyeColor dyeColor = getCurrentStack().getOrDefault(DataComponents.BASE_COLOR, DyeColor.PURPLE);
            return ArsNouveau.prefix("textures/" + genericModel.textPathRoot + "/" + genericModel.name + "_" + dyeColor.getName() + ".png");
        }

        return super.getTextureLocation(instance);
    }
}
