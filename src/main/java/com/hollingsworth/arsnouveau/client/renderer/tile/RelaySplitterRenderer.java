package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.block.tile.ArcaneRelaySplitterTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RelaySplitterRenderer extends TileEntityRenderer<ArcaneRelaySplitterTile> {
    public static final ResourceLocation texture = new ResourceLocation(ArsNouveau.MODID + ":textures/blocks/relay.png");
    public final RelaySplitterModel model = new RelaySplitterModel();

    public RelaySplitterRenderer(TileEntityRendererDispatcher manager) {
        super(manager);
    }

    @Override
    public void render(ArcaneRelaySplitterTile arcaneRelayTile, float f, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
        ms.push();
        ms.translate(0.5F, 1.5F, 0.5F);
        ms.scale(1F, -1F, -1F);
        float angle1 = 0;

        ms.rotate(Vector3f.YP.rotationDegrees(angle1));
        IVertexBuilder buffer = buffers.getBuffer(model.getRenderType(texture));
        model.render(ms, buffer, light, overlay, 1, 1, 1, 1, 1);

        int time = (int) (ClientInfo.ticksInGame + f);
        float angle = (time/10.0f) % 360;
        float outerAngle = (time/20.0f) % 360;

        model.ring_outer.rotateAngleX = outerAngle;
        model.ring_outer.rotateAngleZ = outerAngle;
        model.ring_inner.rotateAngleY = angle;
        model.ring_inner.rotateAngleX = angle;
        model.center.rotateAngleX = -angle;
        model.center.rotateAngleY = angle;
        ms.pop();
    }

    @Override
    public boolean isGlobalRenderer(ArcaneRelaySplitterTile arcaneRelaySplitterTile) {
        return false;
    }

    public static class ISRender extends ItemStackTileEntityRenderer{
        public final RelaySplitterModel model = new RelaySplitterModel();

        @Override
        public void render(ItemStack itemStack, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
            ms.push();
            ms.translate(0.75, -0.65, 0.2);
            IVertexBuilder buffer = buffers.getBuffer(model.getRenderType(texture));
            model.render(ms, buffer, light, overlay, 1, 1, 1, 1, 1);
            ms.pop();
        }
    }
}
