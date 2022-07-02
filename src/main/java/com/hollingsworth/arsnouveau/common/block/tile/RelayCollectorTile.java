package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RelayCollectorTile extends RelayTile {

    public RelayCollectorTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.RELAY_COLLECTOR_TILE, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (disabled)
            return;
        if (!level.isClientSide && level.getGameTime() % 20 == 0 && getSource() <= getMaxSource()) {
            List<BlockPos> takeList = SourceUtil.canTakeSourceAny(getBlockPos(), level, 5);

            for (BlockPos pos : takeList) {
                if (this.getSource() >= getMaxSource()) {
                    break;
                }
                if (!level.isLoaded(pos))
                    continue;
                if (pos.equals(this.getToPos()) || pos.equals(this.getFromPos()) || !(level.getBlockEntity(pos) instanceof ISourceTile)) {
                    continue;
                }


                int transferred = transferSource((ISourceTile) level.getBlockEntity(pos), this);
                if (transferred > 0) {
                    ParticleUtil.spawnFollowProjectile(level, pos, this.worldPosition);
                }
            }

        }
    }
}
