package com.hollingsworth.arsnouveau.client.renderer.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.tile.RelaySplitterModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SpellBookRenderer extends ItemStackTileEntityRenderer {
    public final SpellBookModel model = new SpellBookModel();
    public static final ResourceLocation texture = new ResourceLocation(ArsNouveau.MODID + ":textures/entity/spellbook_archmage_final.png");
    public SpellBookRenderer(){ }

    @Override
    public void render(ItemStack p_228364_1_, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
        ms.push();
        ms.translate(0.5, 1, 1.0);
        ms.scale(0.5f, 0.5f, 0.5f);
        ms.rotate(Vector3f.XP.rotation(180));
        IVertexBuilder buffer = buffers.getBuffer(model.getRenderType(texture));
        model.render(ms, buffer, light, overlay, 1, 1, 1, 1, 1);

        ms.pop();
    }

    public SpellBookModel getModel() {
        return model;
    }
}