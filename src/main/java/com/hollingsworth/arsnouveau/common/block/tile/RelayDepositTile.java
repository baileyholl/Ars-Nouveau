package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.source.SourceAccess;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RelayDepositTile extends RelayTile {

    public RelayDepositTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.RELAY_DEPOSIT_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (disabled)
            return;
        if (!level.isClientSide && level.getGameTime() % 20 == 0 && getSource() > 0) {
            List<SourceAccess> posList = SourceUtil.findAcceptors(worldPosition, level, 5);
            for (SourceAccess access : posList) {
                if (this.getSource() <= 0)
                    break;

                if (access.pos().equals(this.getToPos())) {
                    continue;
                }
                if (access.pos().equals(this.getFromPos())) {
                    continue;
                }

                if (transferSource(this.getSourceStorage(), access.cap()) > 0) {
                    updateBlock();
                }
                ParticleUtil.spawnFollowProjectile(level, this.worldPosition, access.pos(), this.getColor());
            }
        }
    }
}
