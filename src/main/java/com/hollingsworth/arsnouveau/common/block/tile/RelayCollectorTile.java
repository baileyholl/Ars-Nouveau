package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RelayCollectorTile extends RelayTile {

    public RelayCollectorTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.RELAY_COLLECTOR_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (disabled)
            return;
        if (!level.isClientSide && level.getGameTime() % 20 == 0 && getSource() <= getMaxSource()) {
            List<ISpecialSourceProvider> takeList = SourceUtil.canTakeSource(getBlockPos(), level, 5);

            for (ISpecialSourceProvider provider : takeList) {
                if (this.getSource() >= getMaxSource()) {
                    break;
                }
                if(this.getToPos() != null && level.isLoaded(this.getToPos()) && level.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, this.getToPos(), null) == provider.getCapability()){
                    continue;
                }
                if(this.getFromPos() != null && level.isLoaded(this.getFromPos()) && level.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, this.getFromPos(), null) == provider.getCapability()){
                    continue;
                }

                int transferred = transferSource(provider.getCapability(), this.getSourceCapability());
                if (transferred > 0) {
                    ParticleUtil.spawnFollowProjectile(level, provider.getCurrentPos(), this.worldPosition, this.getColor());
                }
            }

        }
    }
}
