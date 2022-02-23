package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.items.armor.NoviceArmor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class MageArmorRenderer extends GeoArmorRenderer<NoviceArmor> {
    public MageArmorRenderer() {
        super(new GenericModel("armor", "items"));
//        // The default values are the ones that come with the default armor template in the geckolib blockbench plugin.
//        this.headBone = "bipedHead";
//        this.bodyBone = "bipedBody";
//        this.rightArmBone = "bipedRightArm";
//        this.leftArmBone = "bipedLeftArm";
//        this.rightLegBone = "bipedRightLeg";
//        this.leftLegBone = "bipedLeftLeg";
//        this.rightBootBone = "armorLeftBoot2";
//        this.leftBootBone = "armorLeftBoot";
    }

    @Override
    public void render(GeoModel model, NoviceArmor animatable, float partialTicks, RenderType type, PoseStack matrixStackIn, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
