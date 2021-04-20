package com.hollingsworth.arsnouveau.common.world.tree;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;

public class SupplierBlockStateProvider extends AbstractSupplierBlockStateProvider{
    public SupplierBlockStateProvider(String path) {
        super(ArsNouveau.MODID, path);
    }

    public static final Codec<SupplierBlockStateProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(d -> d.key.getPath()))
            .apply(instance, SupplierBlockStateProvider::new));

    @Override
    protected BlockStateProviderType<?> type() {
        return BlockRegistry.stateProviderType;
    }
}
