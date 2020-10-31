package com.hollingsworth.arsnouveau.client.particle.engine;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

public class CauldronConversion extends TimedEffect{

    BlockPos pos;
    public CauldronConversion(BlockPos loc , ClientWorld world){
        this.pos = loc;
        this.world = world;
    }

    @Override
    public void tick() {
        super.tick();
        System.out.println("ticking");
        if(ticks >= 300){
            this.isDone = true;
            return;
        }

        if(ticks % 60 == 0){
//            ParticleEngine.getInstance().scheduleEffect(new TimedHelix(pos.down(), 3, GlowParticleData.createData(
//                    new ParticleColor(ParticleUtil.r.nextInt(255), ParticleUtil.r.nextInt(255),ParticleUtil.r.nextInt(255))), world));
        }

    }
}
