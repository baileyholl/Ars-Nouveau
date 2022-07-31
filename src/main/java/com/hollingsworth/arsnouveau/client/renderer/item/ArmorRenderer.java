package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.armor.AnimatedMagicArmor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import java.util.Optional;

public class ArmorRenderer extends GeoArmorRenderer<AnimatedMagicArmor> {

    public ArmorRenderer(AnimatedGeoModel<AnimatedMagicArmor> modelProvider) {
        super(modelProvider);
    }

    @Override
    public void render(GeoModel model, AnimatedMagicArmor animatable, float partialTicks, RenderType type, PoseStack matrixStackIn, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        Optional<GeoBone> slimRight = model.getBone("armorRightArmSlim");
        Optional<GeoBone> slimLeft = model.getBone("armorLeftArmSlim");
        slimRight.ifPresent(geoBone -> geoBone.setHidden(true));
        slimLeft.ifPresent(geoBone -> geoBone.setHidden(true));
        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public ResourceLocation getTextureLocation(AnimatedMagicArmor instance) {
        if(getGeoModelProvider() instanceof GenericModel<AnimatedMagicArmor> genericModel){
            return new ResourceLocation(ArsNouveau.MODID, "textures/" + genericModel.textPathRoot + "/" + genericModel.name + "_" + instance.getColor(itemStack) + ".png");
        }

        return super.getTextureLocation(instance);
    }
}
