package com.hollingsworth.arsnouveau.api.client;

import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.client.RenderUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class BeamEvent implements ITimedEvent {
    Vec3d from;
    Vec3d to;
    int numTicks;
    public BeamEvent(Vec3d from, Vec3d to, int numTicks){
        this.from = from;
        this.to = to;
        this.numTicks = numTicks;
    }

    @Override
    public void tick() {}

    @Override
    public boolean isExpired() {
        return this.numTicks <= 0;
    }

    @Override
    public void tick(RenderWorldLastEvent evt, PlayerEntity player, float renderPartialTicks) {
        numTicks--;
        if(numTicks <= 0)
            return;

        RenderUtil.drawLasers(evt, from, to, 0, 0, 0, 100/255f , 100/255f ,100/255f, 100f, player, renderPartialTicks,1.0f);
    }
}
