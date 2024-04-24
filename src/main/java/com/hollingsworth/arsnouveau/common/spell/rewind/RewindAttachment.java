package com.hollingsworth.arsnouveau.common.spell.rewind;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.IContextAttachment;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewindAttachment implements IContextAttachment {
    public static final ResourceLocation ID = new ResourceLocation(ArsNouveau.MODID, "rewind");

    public Map<Long, List<IRewindCallback>> rewindEvents = new HashMap<>();

    public void addRewindEvent(long gameTime, List<IRewindCallback> callbacks){
        rewindEvents.put(gameTime, callbacks);
    }

    public void addRewindEvent(long gameTime, IRewindCallback callback){
        rewindEvents.computeIfAbsent(gameTime, k -> {
            return new ArrayList<>();
        }).add(callback);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
