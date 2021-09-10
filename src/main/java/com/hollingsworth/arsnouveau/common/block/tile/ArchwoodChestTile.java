package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.client.renderer.tile.ArchwoodChestRenderer;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ArchwoodChestTile extends ChestTileEntity {

    public ArchwoodChestTile(){
        super(BlockRegistry.ARCHWOOD_CHEST_TILE);
    }

    public ArchwoodChestTile(TileEntityType type){
        super(type);
    }

    @OnlyIn(Dist.CLIENT)
    public static void setISTER(Item.Properties props, Block block) {
        props.setISTER(() -> () -> new ItemStackTileEntityRenderer() {
            private final TileEntity tile = new ArchwoodChestTile();
            //render
            @Override
            public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrix, IRenderTypeBuffer buffer, int x, int y) {
                ArchwoodChestRenderer.invBlock = block;
                TileEntityRendererDispatcher.instance.renderItem(tile, matrix, buffer, x, y);
                ArchwoodChestRenderer.invBlock = null;
            }

        });
    }

}
