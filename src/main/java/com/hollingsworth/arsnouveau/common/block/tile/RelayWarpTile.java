package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.mana.IManaTile;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

public class RelayWarpTile extends ArcaneRelaySplitterTile{

    public RelayWarpTile(TileEntityType<?> type){
        super(type);
    }

    public RelayWarpTile(){
        super(BlockRegistry.RELAY_WARP_TILE);
    }

    @Override
    public void createParticles(BlockPos from, BlockPos to) {
        if(level.getBlockEntity(to) instanceof RelayWarpTile) {
            ParticleUtil.spawnTouchPacket(level, getBlockPos(), new ParticleColor.IntWrapper(220, 50, 220));
            ParticleUtil.spawnTouchPacket(level, to,  new ParticleColor.IntWrapper(220, 50, 220));
        }else{
            super.createParticles(from, to);
        }
    }

    public int transferMana(IManaTile from, IManaTile to, int fromTransferRate){
        if(to instanceof RelayWarpTile){
            RelayWarpTile toWarp = (RelayWarpTile) to;
            double adjustedDist = BlockUtil.distanceFrom(toWarp.worldPosition, this.worldPosition) - 30;
            double probLoss = adjustedDist / 100.0;
            if(adjustedDist > 0 && level.getRandom().nextFloat() < probLoss){
                int transferRate = getTransferRate(from, to, fromTransferRate);
                if(transferRate == 0)
                    return 0;
                from.removeMana(transferRate);
                int lossyTransfer = Math.max(1, (int) (transferRate * 0.7));
                to.addMana(lossyTransfer);
                return lossyTransfer;
            }
        }
        return super.transferMana(from, to, fromTransferRate);
    }

    @Override
    public boolean closeEnough(BlockPos pos) {
        return level.getBlockEntity(pos) instanceof RelayWarpTile || super.closeEnough(pos);
    }
}
