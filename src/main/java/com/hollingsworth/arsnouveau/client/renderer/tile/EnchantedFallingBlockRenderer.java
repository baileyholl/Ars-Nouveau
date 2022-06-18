package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class EnchantedFallingBlockRenderer extends EntityRenderer<EnchantedFallingBlock> {
    public EnchantedFallingBlockRenderer(EntityRendererProvider.Context p_174112_) {
        super(p_174112_);
        this.shadowRadius = 0.5F;
    }

    public void render(EnchantedFallingBlock pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        BlockState blockstate = pEntity.getBlockState();
        if (true || blockstate.getRenderShape() == RenderShape.MODEL) {
            Level level = pEntity.getLevel();
            if (blockstate != level.getBlockState(pEntity.blockPosition()) && blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                pMatrixStack.pushPose();
                BlockPos blockpos = new BlockPos(pEntity.getX(), pEntity.getBoundingBox().maxY, pEntity.getZ());
                pMatrixStack.translate(-0.5D, 0.0D, -0.5D);
                BlockRenderDispatcher blockrenderdispatcher = Minecraft.getInstance().getBlockRenderer();
                for (net.minecraft.client.renderer.RenderType type : net.minecraft.client.renderer.RenderType.chunkBufferLayers()) {
                    if (ItemBlockRenderTypes.canRenderInLayer(blockstate, type)) {
                        net.minecraftforge.client.ForgeHooksClient.setRenderType(type);
                        BakedModel bakedmodel = blockrenderdispatcher.getBlockModel(pEntity.blockState);
//                        int i = this.blockColors.getColor(pState, (BlockAndTintGetter)null, (BlockPos)null, 0);
                        float f = 255;
                        float f1 = 1;
                        float f2 = 1;
                        blockrenderdispatcher.getModelRenderer().renderModel(pMatrixStack.last(),
                                pBuffer.getBuffer(ItemBlockRenderTypes.getRenderType(pEntity.blockState, false)),
                                pEntity.blockState, bakedmodel, f, f1, f2, pPackedLight,  OverlayTexture.NO_OVERLAY, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
                    }
                }
                net.minecraftforge.client.ForgeHooksClient.setRenderType(null);
                if(pEntity.getEntityData().get(EnchantedFallingBlock.SHOULD_COLOR)){
//                    RenderSystem.setShaderColor(
//                           255,
//                            pEntity.getParticleColorWrapper().g,
//                            pEntity.getParticleColorWrapper().b,
//                            1.0f
//                    );

                }
                pMatrixStack.popPose();
                super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
            }
        }
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(EnchantedFallingBlock pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
