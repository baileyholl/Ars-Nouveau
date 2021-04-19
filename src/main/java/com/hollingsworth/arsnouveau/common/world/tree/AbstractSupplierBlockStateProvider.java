package com.hollingsworth.arsnouveau.common.world.tree;


import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;
import java.util.function.Function;

public abstract class AbstractSupplierBlockStateProvider extends BlockStateProvider {
    public static <T extends AbstractSupplierBlockStateProvider> Codec<T> codecBuilder(Function<ResourceLocation, T> builder) {
        return ResourceLocation.CODEC.fieldOf("key").xmap(builder, (provider) -> provider.key).codec();
    }

    protected final ResourceLocation key;
    protected BlockState state = null;

    public AbstractSupplierBlockStateProvider(String namespace, String path) {
        this(new ResourceLocation(namespace, path));
    }

    public AbstractSupplierBlockStateProvider(ResourceLocation key) {
        this.key = key;
    }

    @Override
    protected abstract BlockStateProviderType<?> type();

    @Override
    public BlockState getState(Random randomIn, BlockPos blockPosIn) {
        if (state == null) {
            Block block = ForgeRegistries.BLOCKS.getValue(key);
            if (block == null) {
                System.out.println("Block couldn't be located for key: " + key);
                state = Blocks.AIR.defaultBlockState();
            } else {
                state = block.defaultBlockState();
            }
        }

        return state;
    }
}