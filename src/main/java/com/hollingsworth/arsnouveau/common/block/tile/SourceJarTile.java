package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SourceJarTile extends AbstractSourceMachine implements ITooltipProvider, ITickable {

    public SourceJarTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SOURCE_JAR_TILE.get(), pos, state);
    }

    public SourceJarTile(BlockEntityType<? extends SourceJarTile> tileTileEntityType, BlockPos pos, BlockState state) {
        super(tileTileEntityType, pos, state);
    }

    @Override
    protected @NotNull ISourceCap createDefaultSourceCapability() {
        return new SourceStorage(10000, 10000){
            @Override
            public void onContentsChanged() {
                SourceJarTile.this.updateBlock();
            }
        };
    }

    @Override
    public boolean updateBlock() {
        super.updateBlock();
        BlockState state = level.getBlockState(worldPosition);
        int fillState = 0;
        if (this.getSource() > 0 && this.getSource() < 1000)
            fillState = 1;
        else if (this.getSource() != 0) {
            fillState = (this.getSource() / 1000) + 1;
        }
        if (state.hasProperty(SourceJar.fill))
            level.setBlock(worldPosition, state.setValue(SourceJar.fill, Math.min(fillState, 11)), 3);
        return true;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("ars_nouveau.source_jar.fullness", (getSource() * 100) / this.getMaxSource()));
    }
}
