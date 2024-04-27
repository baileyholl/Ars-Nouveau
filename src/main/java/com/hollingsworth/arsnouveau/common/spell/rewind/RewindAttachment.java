package com.hollingsworth.arsnouveau.common.spell.rewind;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.IContextAttachment;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class RewindAttachment implements IContextAttachment {
    public static final ResourceLocation ID = new ResourceLocation(ArsNouveau.MODID, "rewind");

    public Map<Long, List<IRewindCallback>> rewindEvents = new HashMap<>();

    public void addRewindEvents(long gameTime, Collection<IRewindCallback> callbacks){
        rewindEvents.computeIfAbsent(gameTime, k -> new ArrayList<>()).addAll(callbacks);
    }

    public void addRewindEvent(long gameTime, IRewindCallback callback){
        rewindEvents.computeIfAbsent(gameTime, k -> new ArrayList<>()).add(callback);
    }

    public static RewindAttachment get(SpellContext context){
        return context.getOrCreateAttachment(ID, new RewindAttachment());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
