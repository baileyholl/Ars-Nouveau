package com.hollingsworth.arsnouveau.common.world.tree;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;

public class SupplierBlockStateProvider extends AbstractSupplierBlockStateProvider {
    public SupplierBlockStateProvider(String path) {
        this(ArsNouveau.prefix( path));
    }

    public SupplierBlockStateProvider(ResourceLocation path) {
        super(path);
    }

    public static final Codec<SupplierBlockStateProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("key").forGetter(d -> d.key.getPath()))
            .apply(instance, SupplierBlockStateProvider::new));

    @Override
    protected BlockStateProviderType<?> type() {
        return BlockRegistry.stateProviderType.get();
    }

}
