package com.hollingsworth.arsnouveau.api.ritual;

import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RitualContext {
    public int progress;
    public boolean isDone;
    public boolean isStarted;
    public boolean needsSourceToRun; // Marks the last time mana was consumed or added.
    public List<ItemStack> consumedItems;

    public RitualContext() {
        progress = 0;
        isDone = false;
        this.isStarted = false;
        consumedItems = new ArrayList<>();
        needsSourceToRun = false;
    }


    public void write(HolderLookup.Provider provider, CompoundTag tag) {
        tag.putInt("progress", progress);
        tag.putBoolean("complete", isDone);
        tag.putBoolean("started", isStarted);
        tag.putBoolean("needsMana", needsSourceToRun);
        NBTUtil.writeItems(provider, tag, "item_", consumedItems);
    }

    public static RitualContext read(HolderLookup.Provider provider, CompoundTag tag) {
        RitualContext context = new RitualContext();
        context.progress = tag.getIntOr("progress", 0);
        context.isDone = tag.getBooleanOr("complete", false);
        context.isStarted = tag.getBooleanOr("started", false);
        context.consumedItems = NBTUtil.readItems(provider, tag, "item_");
        context.needsSourceToRun = tag.getBooleanOr("needsMana", false);
        return context;
    }
}
