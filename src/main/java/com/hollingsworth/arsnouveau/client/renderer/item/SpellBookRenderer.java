package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.common.items.Wand;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SpellBookRenderer extends FixedGeoItemRenderer<Wand> {
    public SpellBookRenderer(){
        super(new SpellBookModel());
    }

    @Override
    public void render(Item animatable, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn, ItemStack itemStack, ItemCameraTransforms.TransformType transformType) {
        super.render(animatable, stack, bufferIn, packedLightIn, itemStack, transformType);
    }
}