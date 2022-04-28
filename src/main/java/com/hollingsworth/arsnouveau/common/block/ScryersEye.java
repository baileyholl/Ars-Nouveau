package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ScryersEyeTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ScryersEye extends TickableModBlock{
    public ScryersEye(Properties properties, String registry) {
        super(properties, registry);
    }

    public ScryersEye(String registryName) {
        this(defaultProperties().noOcclusion(), registryName);
    }

    public ScryersEye(Properties properties) {
        super(properties);
    }



    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ScryersEyeTile(pPos ,pState);
    }
}
