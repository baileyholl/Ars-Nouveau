package com.hollingsworth.arsnouveau.client.renderer;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.ArcaneRelayTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RelayRenderer extends TileEntityRenderer<ArcaneRelayTile> {
    public static final ResourceLocation texture = new ResourceLocation(ArsNouveau.MODID + ":textures/blocks/relay.png");
    public static final RelayModel model = new RelayModel();

    public RelayRenderer(TileEntityRendererDispatcher manager) {
        super(manager);
    }

    @Override
    public void render(ArcaneRelayTile arcaneRelayTile, float f, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
        ms.push();
        ms.translate(0.5F, 1.5F, 0.5F);
        ms.scale(1F, -1F, -1F);
        float angle = 0;

        ms.rotate(Vector3f.YP.rotationDegrees(angle));
//        float fract = Math.max(0.1F, 1F - (bellows == null ? 0 : bellows.movePos + bellows.moving * f + 0.1F));
        IVertexBuilder buffer = buffers.getBuffer(model.getRenderType(texture));
        model.render(ms, buffer, light, overlay, 1, 1, 1, 1, 1);
        ms.pop();
    }

    @Override
    public boolean isGlobalRenderer(ArcaneRelayTile p_188185_1_) {
        return false;
    }

    public static class ISRender extends ItemStackTileEntityRenderer{

        public ISRender(){ }

        @Override
        public void render(ItemStack p_228364_1_, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
            ms.push();
            ms.translate(0.75, -0.65, 0.2);
            IVertexBuilder buffer = buffers.getBuffer(model.getRenderType(texture));
            model.render(ms, buffer, light, overlay, 1, 1, 1, 1, 1);
            ms.pop();
        }
    }
}
