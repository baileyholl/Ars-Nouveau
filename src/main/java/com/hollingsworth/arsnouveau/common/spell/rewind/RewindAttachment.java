package com.hollingsworth.arsnouveau.common.spell.rewind;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.IContextAttachment;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;

public class RewindAttachment implements IContextAttachment {
    public static final ResourceLocation ID = new ResourceLocation(ArsNouveau.MODID, "rewind");

    private final Map<Long, List<IRewindCallback>> rewindEvents = new HashMap<>();
    private long lockedTime = 0;

    public void addRewindEvents(long gameTime, Collection<IRewindCallback> callbacks){
        if(lockedTime == gameTime){
            return;
        }
        rewindEvents.computeIfAbsent(gameTime, k -> new ArrayList<>()).addAll(callbacks);
    }

    public void addRewindEvent(long gameTime, IRewindCallback callback){
        if(lockedTime == gameTime){
            return;
        }
        rewindEvents.computeIfAbsent(gameTime, k -> new ArrayList<>()).add(callback);
    }

    public static RewindAttachment get(SpellContext context){
        return context.getOrCreateAttachment(ID, new RewindAttachment());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public Map<Long, List<IRewindCallback>> getRewindEvents() {
        return rewindEvents;
    }

    public @Nullable List<IRewindCallback> getForTime(long gameTime){
        return rewindEvents.get(gameTime);
    }

    public long setLockedTime(long lockedTime){
        this.lockedTime = lockedTime;
        return lockedTime;
    }

    public long getLockedTime(){
        return lockedTime;
    }
}
