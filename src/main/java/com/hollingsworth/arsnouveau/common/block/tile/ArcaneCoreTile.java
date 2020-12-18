package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class ArcaneCoreTile extends TileEntity implements ITickableTileEntity {
    public ArcaneCoreTile() {
        super(BlockRegistry.ARCANE_CORE_TILE);
    }


    @Override
    public void tick() {

        ParticleColor randColor = new ParticleColor(world.rand.nextInt(255), world.rand.nextInt(255), world.rand.nextInt(255));
        for(int i = 0; i < 6 ; i++) {
            world.addParticle(
                    GlowParticleData.createData(randColor),
                    pos.getX() + 0.5 + ParticleUtil.inRange(-0.3, 0.3), pos.getY() + 0.5 + ParticleUtil.inRange(-0.3, 0.3), pos.getZ() + 0.5 + ParticleUtil.inRange(-0.3, 0.3),
                    0, 0, 0);
        }
    }
}
