package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
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
        if(disabled)
            return;
        if(!level.isClientSide && level.getGameTime() % 20 == 0 && getSource() > 0){
            List<BlockPos> posList = SourceUtil.canGiveSourceAny(worldPosition, level, 5);
            for(BlockPos jarPos : posList) {
                if(this.getSource() == 0)
                    break;
                if(!level.isLoaded(jarPos))
                    continue;

                if (jarPos != null && !jarPos.equals(this.getToPos()) && !jarPos.equals(this.getFromPos()) && level.getBlockEntity(jarPos) instanceof SourceJarTile) {
                    transferSource(this, (ISourceTile) level.getBlockEntity(jarPos));
                    ParticleUtil.spawnFollowProjectile(level, this.worldPosition, jarPos);
                }
            }
        }
    }
}
