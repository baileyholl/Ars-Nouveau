package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.entity.DiscEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DiscRenderer extends GeoEntityRenderer<DiscEntity> {

    public DiscRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GenericModel<>("terra_top").withEmptyAnim());
    }

    @Override
    public void render(DiscEntity entityIn, float pEntityYaw, float pPartialTick, PoseStack poseStack, MultiBufferSource bufferSource, int pPackedLight) {
        BlockState state = Blocks.DIRT.defaultBlockState();
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, bufferSource, pPackedLight, OverlayTexture.NO_OVERLAY);
    }

    @Override
    public ResourceLocation getTextureLocation(DiscEntity entity) {
        return ResourceLocation.withDefaultNamespace("textures/block/dirt.png");
    }

}
