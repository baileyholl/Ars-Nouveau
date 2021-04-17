package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.ManaCondenserTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class ManaCondenserRenderer extends TileEntityRenderer<ManaCondenserTile> {

    public static final ResourceLocation texture = new ResourceLocation(ArsNouveau.MODID + ":textures/blocks/mana_condenser.png");
    public static final ManaCondenserModel model = new ManaCondenserModel();


    public ManaCondenserRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Override
    public void render(ManaCondenserTile manaCondenserTile, float v, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
        ms.pushPose();
        ms.translate(0.5F, 1.5F, 0.5F);
        ms.scale(1F, -1F, -1F);
        IVertexBuilder buffer = buffers.getBuffer(model.renderType(texture));
        model.renderToBuffer(ms, buffer, light, overlay, 1, 1, 1, 1);
        ms.popPose();
    }

    public static class ISRender extends ItemStackTileEntityRenderer {

        public ISRender(){ }

        @Override
        public void renderByItem(ItemStack p_239207_1_, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
            ms.pushPose();
            ms.translate(0.5, 1.5, 0.5);
            ms.mulPose(Vector3f.YP.rotationDegrees(180));
            ms.mulPose(Vector3f.ZP.rotationDegrees(0));
            ms.mulPose(Vector3f.XP.rotationDegrees(180));
            IVertexBuilder buffer = buffers.getBuffer(model.renderType(texture));
            model.renderToBuffer(ms, buffer, light, overlay, 1, 1, 1, 1);

            ms.popPose();
        }
    }
}
