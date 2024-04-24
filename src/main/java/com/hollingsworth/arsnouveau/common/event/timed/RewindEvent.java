package com.hollingsworth.arsnouveau.common.event.timed;

import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.spell.rewind.IRewindCallback;
import com.hollingsworth.arsnouveau.common.spell.rewind.RewindAttachment;
import com.hollingsworth.arsnouveau.common.spell.rewind.RewindEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class RewindEvent implements ITimedEvent {

    public Entity entity;
    public boolean doneRewinding;
    public int rewindTicks;
    public int ticksToRewind;
    public boolean respectsGravity;
    public @Nullable SpellContext context;
    public boolean serverSide;

    public RewindEvent(Entity entity, int ticksToRewind) {
        this.entity = entity;
        this.ticksToRewind = ticksToRewind;
        respectsGravity = !entity.isNoGravity();
    }

    public RewindEvent(Entity entity, int ticksToRewind, SpellContext context){
        this(entity, ticksToRewind);
        this.context = context;
    }

    @Override
    public void tick(boolean serverSide) {
        this.serverSide = serverSide;
        if(!(entity instanceof IRewindable rewindable) || (entity instanceof LivingEntity living && living.isDeadOrDying()) || entity.isRemoved()){
            doneRewinding = true;
            return;
        }
        if(rewindable.getMotions().empty()){
            stop(rewindable);
            return;
        }
        RewindEntityData data = rewindable.getMotions().pop();
        rewindable.setRewinding(true);
        if(context != null && this.context.attachments.get(RewindAttachment.ID) instanceof RewindAttachment rewindAttachment){
            List<IRewindCallback> contextData = rewindAttachment.rewindEvents.get(data.gameTime);
            if(contextData != null){
                for(IRewindCallback callback : contextData){
                    callback.onRewind(this);
                }
            }
        }
        data.onRewind(this);
        rewindTicks++;
        if(rewindTicks >= ticksToRewind){
            stop(rewindable);
        }
    }

    public void stop(IRewindable rewindable){
        doneRewinding = true;
        rewindable.setRewinding(false);
        entity.setDeltaMovement(Vec3.ZERO);
        if(respectsGravity) {
            entity.setNoGravity(false);
        }
    }

    @Override
    public void onServerStopping() {
        if(respectsGravity){
            entity.setNoGravity(false);
        }
    }

    @Override
    public boolean isExpired() {
        return doneRewinding;
    }
}
