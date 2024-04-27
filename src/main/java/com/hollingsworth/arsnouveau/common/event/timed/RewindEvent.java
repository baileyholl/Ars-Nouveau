package com.hollingsworth.arsnouveau.common.event.timed;

import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.spell.rewind.IRewindCallback;
import com.hollingsworth.arsnouveau.common.spell.rewind.RewindAttachment;
import com.hollingsworth.arsnouveau.common.spell.rewind.RewindEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class RewindEvent implements ITimedEvent {

    public @Nullable Entity entity;
    public boolean doneRewinding;
    public int rewindTicks;
    public int ticksToRewind;
    public boolean respectsGravity;
    public @Nullable SpellContext context;
    public boolean serverSide;
    public long startGameTime;

    public RewindEvent(long gameTime, int ticksToRewind, @Nullable SpellContext spellContext){
        this.startGameTime = gameTime;
        this.ticksToRewind = ticksToRewind;
        this.context = spellContext;
    }

    public RewindEvent(@Nullable Entity entity, long gameTime, int ticksToRewind) {
        this(gameTime, ticksToRewind, null);
        this.entity = entity;
        respectsGravity = entity != null && !entity.isNoGravity();
    }

    public RewindEvent(@Nullable Entity entity, long gameTime, int ticksToRewind, @Nullable SpellContext context){
        this(entity, gameTime, ticksToRewind);
        this.context = context;
    }

    @Override
    public void tick(boolean serverSide) {
        this.serverSide = serverSide;
        long eventGameTime = startGameTime - this.rewindTicks;
        if(entity instanceof IRewindable rewindable){
            rewindable.setRewinding(true);
            if(!rewindable.getMotions().empty()){
                RewindEntityData data = rewindable.getMotions().pop();
                data.onRewind(this);
            }
        }
        if(context != null){
            RewindAttachment rewindAttachment = RewindAttachment.get(context);
            List<IRewindCallback> contextData = rewindAttachment.rewindEvents.get(eventGameTime);
            if(contextData != null){
                for(IRewindCallback callback : contextData){
                    callback.onRewind(this);
                }
            }
        }
        rewindTicks++;
        if(rewindTicks >= ticksToRewind){
            stop();
        }
    }

    public void stop(){
        doneRewinding = true;
        if(entity instanceof IRewindable rewindable){
            rewindable.setRewinding(false);
            entity.setDeltaMovement(Vec3.ZERO);
            if(respectsGravity) {
                entity.setNoGravity(false);
            }
        }
    }

    @Override
    public void onServerStopping() {
        if(respectsGravity && entity != null){
            entity.setNoGravity(false);
        }
    }

    @Override
    public boolean isExpired() {
        return doneRewinding;
    }
}
