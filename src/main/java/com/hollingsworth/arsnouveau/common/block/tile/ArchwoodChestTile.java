package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.client.renderer.tile.ArchwoodChestRenderer;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ArchwoodChestTile extends ChestBlockEntity {

    public ArchwoodChestTile(BlockPos pos, BlockState state){
        super(BlockRegistry.ARCHWOOD_CHEST_TILE, pos, state);
    }

    public ArchwoodChestTile(BlockEntityType type, BlockPos pos, BlockState state){
        super(type, pos, state);
    }


//    @OnlyIn(Dist.CLIENT)
//    public static void setISTER(Item.Properties props, Block block) {
//        props.setISTER(() -> () -> new BlockEntityWithoutLevelRenderer() {
//            private final BlockEntity tile = new ArchwoodChestTile();
//            //render
//            @Override
//            public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrix, MultiBufferSource buffer, int x, int y) {
//                ArchwoodChestRenderer.invBlock = block;
//                BlockEntityRenderDispatcher.instance.renderItem(tile, matrix, buffer, x, y);
//                ArchwoodChestRenderer.invBlock = null;
//            }
//
//        });
//    }

}
