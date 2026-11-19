package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.source.SourceAccess;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
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
            List<SourceAccess> takeList = SourceUtil.findProviders(getBlockPos(), level, 5);

            for (SourceAccess access : takeList) {
                if (this.getSource() >= getMaxSource()) {
                    break;
                }
                if (access.pos().equals(this.getToPos())) {
                    continue;
                }
                if (access.pos().equals(this.getFromPos())) {
                    continue;
                }

                int transferred = transferSource(access.cap(), this.getSourceStorage());
                if (transferred > 0) {
                    updateBlock();
                    ParticleUtil.spawnFollowProjectile(level, access.pos(), this.worldPosition, this.getColor());
                }
            }

        }
    }
}
