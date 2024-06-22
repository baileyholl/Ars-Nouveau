package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.ScryersOculusTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;


public class ScryerOculusRenderer extends ArsGeoBlockRenderer<ScryersOculusTile> {

    public ScryerOculusRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn, ScryersEyeModel model) {
        super(rendererDispatcherIn, model);
    }



    @Override
    public void actuallyRender(PoseStack poseStack, ScryersOculusTile pBlockEntity, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        GeoBone eye = this.getGeoModel().getBone("eye").orElse(null);
        if (eye == null)
            return;

        // Taken from enchantment table
        float f1;
        for (f1 = pBlockEntity.rot - pBlockEntity.oRot; f1 >= (float) Math.PI; f1 -= ((float) Math.PI * 2F)) {
        }

        while (f1 < -(float) Math.PI) {
            f1 += ((float) Math.PI * 2F);
        }
        float f2 = pBlockEntity.oRot + f1 * ClientInfo.partialTicks - 4.7f;
        eye.setRotY(-f2);
        eye.setPosY((Mth.sin((ClientInfo.ticksInGame + ClientInfo.partialTicks) / 10.0f)) / 2f);
        super.actuallyRender(poseStack, pBlockEntity, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(new ScryersEyeModel());
    }
}
