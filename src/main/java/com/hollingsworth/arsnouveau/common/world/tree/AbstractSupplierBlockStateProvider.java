package com.hollingsworth.arsnouveau.common.world.tree;


import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;

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
    public BlockState getState(RandomSource randomIn, BlockPos blockPosIn) {
        if (state == null) {
            Block block = ForgeRegistries.BLOCKS.getValue(key);
            if (block == null) {
                state = Blocks.AIR.defaultBlockState();
            } else {
                state = block.defaultBlockState();
            }
        }

        return state;
    }
}