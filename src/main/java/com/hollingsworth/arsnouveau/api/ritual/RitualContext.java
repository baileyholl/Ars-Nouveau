package com.hollingsworth.arsnouveau.api.ritual;

import net.minecraft.nbt.CompoundNBT;

public class RitualContext {
    public int progress;
    public boolean isDone;

    public RitualContext(){
        progress = 0;
    }


    public void write(CompoundNBT tag){
        tag.putInt("progress", progress);
        tag.putBoolean("complete", isDone);

    }

    public static RitualContext read(CompoundNBT tag){
        RitualContext context = new RitualContext();
        context.progress = tag.getInt("progress");
        context.isDone = tag.getBoolean("complete");
        return context;
    }
}
