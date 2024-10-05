package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class SourceJarTile extends AbstractSourceMachine implements ITooltipProvider, ITickable {

    public SourceJarTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SOURCE_JAR_TILE.get(), pos, state);
    }

    public SourceJarTile(BlockEntityType<? extends SourceJarTile> tileTileEntityType, BlockPos pos, BlockState state) {
        super(tileTileEntityType, pos, state);
    }

    @Override
    public int getMaxSource() {
        return 10000;
    }

    @Override
    public boolean updateBlock() {
        super.updateBlock();
        BlockState state = level.getBlockState(worldPosition);
        int fillState = 0;
        if (this.getSource() > 0 && getMaxSource() != 0)
            fillState = 1 + 10 * (int) Math.clamp((float) this.getSource() / getMaxSource(), 0, 1);
        if (state.hasProperty(SourceJar.fill))
            level.setBlock(worldPosition, state.setValue(SourceJar.fill, fillState), 3);
        return true;
    }

    @Override
    public int getTransferRate() {
        return getMaxSource();
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("ars_nouveau.source_jar.fullness", (getSource() * 100) / this.getMaxSource()));
    }
}
