package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ArchwoodChestTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ArchwoodChest extends ChestBlock {
    public ArchwoodChest() {
        super(BlockBehaviour.Properties.of().strength(2.5F).sound(SoundType.WOOD), () -> BlockRegistry.ARCHWOOD_CHEST_TILE.get());
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ArchwoodChestTile(pos, state);
    }

    public static class Item extends BlockItem {

        public Item(Block block, net.minecraft.world.item.Item.Properties props) {
            super(block, props);
        }

        @Override
        public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
            super.initializeClient(consumer);
            consumer.accept(new IClientItemExtensions() {
                @Override
                public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    Minecraft mc = Minecraft.getInstance();

                    return new BlockEntityWithoutLevelRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels()) {
                        private final BlockEntity tile = new ArchwoodChestTile(BlockPos.ZERO, getBlock().defaultBlockState());

                        @Override
                        public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext transformType, @NotNull PoseStack pose, @NotNull MultiBufferSource buffer, int x, int y) {
                            mc.getBlockEntityRenderDispatcher().renderItem(tile, pose, buffer, x, y);
                        }

                    };
                }
            });
        }
    }

}
