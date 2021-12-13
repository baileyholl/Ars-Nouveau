package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class SourceJarTile extends AbstractSourceMachine implements ITickable, ITooltipProvider {

    public SourceJarTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SOURCE_JAR_TILE, pos, state);
    }

    public SourceJarTile(BlockEntityType<? extends SourceJarTile> tileTileEntityType, BlockPos pos, BlockState state){
        super(tileTileEntityType, pos, state);
    }

    @Override
    public int getMaxSource() {
        return 10000;
    }

    @Override
    public void tick() {
        if(level.isClientSide) {
            // world.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
            return;
        }
        BlockState state = level.getBlockState(worldPosition);
        int fillState = 0;
        if(this.getSource() > 0 && this.getSource() < 1000)
            fillState = 1;
        else if(this.getSource() != 0){
            fillState = (this.getSource() / 1000) + 1;
        }

        level.setBlock(worldPosition, state.setValue(SourceJar.fill, fillState),3);
    }


    @Override
    public int getTransferRate() {
        return getMaxSource();
    }

    @Override
    public List<Component> getTooltip(List<Component> tooltip) {
        tooltip.add(new TranslatableComponent("ars_nouveau.source_jar.fullness", (getSource()*100) / this.getMaxSource()));
        return tooltip;
    }
}
