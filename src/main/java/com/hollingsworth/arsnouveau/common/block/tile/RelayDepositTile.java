package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RelayDepositTile extends RelayTile {

    public RelayDepositTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.RELAY_DEPOSIT_TILE, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (disabled)
            return;
        if (!level.isClientSide && level.getGameTime() % 20 == 0 && getSource() > 0) {
            List<ISpecialSourceProvider> posList = SourceUtil.canGiveSource(worldPosition, level, 5);
            for (ISpecialSourceProvider provider : posList) {
                if (this.getSource() <= 0)
                    break;

                if(this.getToPos() != null && level.isLoaded(this.getToPos()) && level.getBlockEntity(this.getToPos()) == provider.getSource()){
                    continue;
                }
                if(this.getFromPos() != null && level.isLoaded(this.getFromPos()) && level.getBlockEntity(this.getFromPos()) == provider.getSource()){
                    continue;
                }

                if (!(provider.getSource() instanceof RelayTile)) {
                    transferSource(this, provider.getSource());
                    ParticleUtil.spawnFollowProjectile(level, this.worldPosition, provider.getCurrentPos(), this.getColor());
                }
            }
        }
    }
}
