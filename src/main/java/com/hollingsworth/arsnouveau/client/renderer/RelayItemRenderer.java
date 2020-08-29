package com.hollingsworth.arsnouveau.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mezz.jei.plugins.vanilla.ingredients.item.ItemStackRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

public class RelayItemRenderer extends ItemStackTileEntityRenderer {
    @Override
    public void render(ItemStack p_228364_1_, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
        super.render(p_228364_1_, ms, buffers, light, overlay);
        System.out.println("rendering");
        ms.push();
        IVertexBuilder buffer = buffers.getBuffer(RelayRenderer.model.getRenderType(RelayRenderer.texture));
        RelayRenderer.model.render(ms, buffer, light, overlay, 1, 1, 1, 1, 1);
        ms.pop();
    }
}
