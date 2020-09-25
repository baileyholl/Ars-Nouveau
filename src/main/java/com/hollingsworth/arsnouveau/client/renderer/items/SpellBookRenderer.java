package com.hollingsworth.arsnouveau.client.renderer.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.tile.RelaySplitterModel;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SpellBookRenderer extends ItemStackTileEntityRenderer {
    public final SpellBookModel model = new SpellBookModel();
    public static final ResourceLocation arch = new ResourceLocation(ArsNouveau.MODID + ":textures/entity/spellbook_archmage_final.png");
    public static final ResourceLocation apprentice = new ResourceLocation(ArsNouveau.MODID + ":textures/entity/spellbook_mage_final.png");
    public static final ResourceLocation novice = new ResourceLocation(ArsNouveau.MODID + ":textures/entity/spellbook_novice_final.png");

    public SpellBookRenderer(){ }

    @Override
    public void render(ItemStack stack, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
        ms.push();
        ms.translate(0.5, 1, 1.0);
        ms.scale(0.5f, 0.5f, 0.5f);
        ms.rotate(Vector3f.XP.rotation(180));
        ResourceLocation location = stack.getItem() == ItemsRegistry.noviceSpellBook ? novice : stack.getItem() == ItemsRegistry.apprenticeSpellBook ? apprentice : arch;
        IVertexBuilder buffer = buffers.getBuffer(model.getRenderType(location));
        model.render(ms, buffer, light, overlay, 1, 1, 1, 1, 1);

        ms.pop();
    }

    public SpellBookModel getModel() {
        return model;
    }
}