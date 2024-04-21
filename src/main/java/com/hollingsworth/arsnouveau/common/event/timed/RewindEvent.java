package com.hollingsworth.arsnouveau.common.event.timed;

import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketWarpPosition;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectRewind;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class RewindEvent implements ITimedEvent {

    public Entity entity;
    boolean doneRewinding;
    public int rewindTicks;
    public int ticksToRewind;

    public RewindEvent(Entity entity) {
        this.entity = entity;
        if(entity instanceof IRewindable rewindable){
            ticksToRewind = rewindable.getMotions().size();
            for(EffectRewind.Data vec3 : rewindable.getMotions()){
                System.out.println(vec3);
            }
//            System.out.println(rewindable.getMotions().size());
        }
    }

    @Override
    public void tick(boolean serverSide) {
        if(!(entity instanceof IRewindable rewindable) || rewindable.getMotions().empty()){
            doneRewinding = true;
            return;
        }
        EffectRewind.Data data = rewindable.getMotions().pop();
        rewindable.setRewinding(true);
        entity.hurtMarked = true;
        rewindable.setRewinding(false);
        entity.setNoGravity(true);
        entity.setPos(data.position());
        entity.setDeltaMovement(data.deltaMovement().scale(-1));
        Vec3 warpPos = data.position();
        entity.teleportTo(warpPos.x(), warpPos.y(), warpPos.z());
        Networking.sendToNearby(entity.level, entity, new PacketWarpPosition(entity.getId(), entity.getX(), entity.getY(), entity.getZ(), entity.getXRot(), entity.getYRot()));
        rewindable.setRewinding(true);
        rewindTicks++;
        if(rewindTicks >= ticksToRewind){
            doneRewinding = true;
            rewindable.setRewinding(false);
            entity.setDeltaMovement(Vec3.ZERO);
            entity.setNoGravity(false);
        }
    }

    @Override
    public boolean isExpired() {
        return doneRewinding;
    }
}
