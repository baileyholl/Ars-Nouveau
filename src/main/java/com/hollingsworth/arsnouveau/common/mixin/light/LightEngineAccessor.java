package com.hollingsworth.arsnouveau.common.mixin.light;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LightEngine.class)
public interface LightEngineAccessor<M extends DataLayerStorageMap<M>, S extends LayerLightSectionStorage<M>> {
    @Accessor
    S getStorage();

    @Invoker
    boolean callShapeOccludes(long sourcePackedPos, BlockState sourceState, long targetPackedPos, BlockState targetState, Direction direction);

    @Invoker
    void callEnqueueIncrease(long packedPos, long flags);

    @Invoker
    void callEnqueueDecrease(long packedPos, long flags);

    @Invoker
    BlockState callGetState(BlockPos pos);

    @Accessor
    LightChunkGetter getChunkSource();
}
