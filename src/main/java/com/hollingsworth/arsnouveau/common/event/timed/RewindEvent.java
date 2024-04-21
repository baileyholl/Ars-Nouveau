package com.hollingsworth.arsnouveau.common.event.timed;

import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectRewind;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class RewindEvent implements ITimedEvent {

    public Entity entity;
    boolean doneRewinding;
    public int rewindTicks;
    public int ticksToRewind;

    public RewindEvent(Entity entity, int ticksToRewind) {
        this.entity = entity;
        this.ticksToRewind = ticksToRewind;
    }

    @Override
    public void tick(boolean serverSide) {
        if(!(entity instanceof IRewindable rewindable)){
            doneRewinding = true;
            return;
        }
        if(rewindable.getMotions().empty()){
            stop(rewindable);
            return;
        }
        EffectRewind.Data data = rewindable.getMotions().pop();
        rewindable.setRewinding(true);
        entity.hurtMarked = true;
        rewindable.setRewinding(false);
        entity.setNoGravity(true);
        entity.setPos(data.position());
        entity.setDeltaMovement(data.deltaMovement().scale(-1));
//        Vec3 warpPos = data.position();
//        entity.teleportTo(warpPos.x(), warpPos.y(), warpPos.z());
//        Networking.sendToNearby(entity.level, entity, new PacketWarpPosition(entity.getId(), entity.getX(), entity.getY(), entity.getZ(), entity.getXRot(), entity.getYRot()));
        rewindable.setRewinding(true);
        rewindTicks++;
        if(rewindTicks >= ticksToRewind){
            stop(rewindable);
        }
    }

    public void stop(IRewindable rewindable){
        doneRewinding = true;
        rewindable.setRewinding(false);
        entity.setDeltaMovement(Vec3.ZERO);
        entity.setNoGravity(false);
    }

    @Override
    public boolean isExpired() {
        return doneRewinding;
    }
}
