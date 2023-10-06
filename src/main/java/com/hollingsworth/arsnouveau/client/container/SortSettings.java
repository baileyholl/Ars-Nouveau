package com.hollingsworth.arsnouveau.client.container;

import net.minecraft.nbt.CompoundTag;

public class SortSettings {
    public int controlMode;
    public boolean reverseSort;
    public int sortType;
    public int searchType;
    public boolean expanded;

    public SortSettings(int controlMode, boolean reverseSort, int sortType, int searchType, boolean collapse) {
        this.controlMode = controlMode;
        this.reverseSort = reverseSort;
        this.sortType = sortType;
        this.searchType = searchType;
        this.expanded = collapse;
    }

    public SortSettings(){}

    public CompoundTag toTag(){
        CompoundTag updateTag = new CompoundTag();
        updateTag.putInt("controlMode", controlMode);
        updateTag.putBoolean("reverse", reverseSort);
        updateTag.putInt("sortType", sortType);
        updateTag.putInt("searchType", searchType);
        updateTag.putBoolean("expanded", expanded);
        return updateTag;
    }

    public static SortSettings fromTag(CompoundTag tag){
        return new SortSettings(tag.getInt("controlMode"), tag.getBoolean("reverse"), tag.getInt("sortType"), tag.getInt("searchType"), tag.getBoolean("expanded"));
    }
}
