package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RelayWarpTile extends RelaySplitterTile {

    public RelayWarpTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public RelayWarpTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.RELAY_WARP_TILE.get(), pos, state);
    }

    @Override
    public void createParticles(BlockPos from, BlockPos to) {
        if (level.getBlockEntity(to) instanceof RelayWarpTile) {
            ParticleUtil.spawnTouchPacket(level, getBlockPos(), new ParticleColor(220, 50, 220));
            ParticleUtil.spawnTouchPacket(level, to, new ParticleColor(220, 50, 220));
        } else {
            super.createParticles(from, to);
        }
    }

    public int transferSource(ISourceTile from, ISourceTile to, int fromTransferRate) {
        if (to instanceof RelayWarpTile toWarp) {
            double adjustedDist = BlockUtil.distanceFrom(toWarp.worldPosition, this.worldPosition) - 30;
            double probLoss = adjustedDist / 100.0;
            if (adjustedDist > 0 && level.getRandom().nextFloat() < probLoss) {
                int transferRate = getTransferRate(from, to, fromTransferRate);
                if (transferRate == 0)
                    return 0;
                from.removeSource(transferRate);
                int lossyTransfer = Math.max(1, (int) (transferRate * 0.7));
                to.addSource(lossyTransfer);
                return lossyTransfer;
            }
        }
        return super.transferSource(from, to, fromTransferRate);
    }

    @Override
    public int transferSource(ISourceCap from, ISourceCap to) {
        if (to instanceof RelayWarpTile toWarp) {
            double adjustedDist = BlockUtil.distanceFrom(toWarp.worldPosition, this.worldPosition) - 30;
            double probLoss = adjustedDist / 100.0;
            if (adjustedDist > 0 && level.getRandom().nextFloat() < probLoss) {
                int transfer = to.receiveSource(from.getSource(), true);
                if (transfer == 0)
                    return 0;
                from.extractSource(transfer, false);
                int lossyTransfer = Math.max(1, (int) (transfer * 0.7));
                to.receiveSource(lossyTransfer, false);
            }
        }
        return super.transferSource(from, to);
    }

    @Override
    public boolean closeEnough(BlockPos pos) {
        return level.getBlockEntity(pos) instanceof RelayWarpTile || super.closeEnough(pos);
    }
}
