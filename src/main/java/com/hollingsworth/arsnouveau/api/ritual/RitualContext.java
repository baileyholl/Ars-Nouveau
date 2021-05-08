package com.hollingsworth.arsnouveau.api.ritual;

import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.List;

public class RitualContext {
    public int progress;
    public boolean isDone;
    public boolean isStarted;
    public boolean needsManaToRun; // Marks the last time mana was consumed or added.
    public List<ItemStack> consumedItems;

    public RitualContext(){
        progress = 0;
        isDone = false;
        this.isStarted = false;
        consumedItems = new ArrayList<>();
        needsManaToRun = false;
    }


    public void write(CompoundNBT tag){
        tag.putInt("progress", progress);
        tag.putBoolean("complete", isDone);
        tag.putBoolean("started", isStarted);
        tag.putBoolean("needsMana", needsManaToRun);
        NBTUtil.writeItems(tag,"item_", consumedItems);
    }

    public static RitualContext read(CompoundNBT tag){
        RitualContext context = new RitualContext();
        context.progress = tag.getInt("progress");
        context.isDone = tag.getBoolean("complete");
        context.isStarted = tag.getBoolean("started");
        context.consumedItems = NBTUtil.readItems(tag, "item_");
        context.needsManaToRun = tag.getBoolean("needsMana");
        return context;
    }
}
