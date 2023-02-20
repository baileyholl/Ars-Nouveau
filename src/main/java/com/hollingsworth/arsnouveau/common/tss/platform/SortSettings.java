package com.hollingsworth.arsnouveau.common.tss.platform;

import net.minecraft.nbt.CompoundTag;

public class SortSettings {
    public int controlMode;
    public boolean reverseSort;
    public int sortType;
    public int searchType;

    public SortSettings(int controlMode, boolean reverseSort, int sortType, int searchType) {
        this.controlMode = controlMode;
        this.reverseSort = reverseSort;
        this.sortType = sortType;
        this.searchType = searchType;
    }

    public SortSettings(){}

    public CompoundTag toTag(){
        CompoundTag updateTag = new CompoundTag();
        updateTag.putInt("controlMode", controlMode);
        updateTag.putBoolean("reverse", reverseSort);
        updateTag.putInt("sortType", sortType);
        updateTag.putInt("searchType", searchType);
        return updateTag;
    }

    public static SortSettings fromTag(CompoundTag tag){
        return new SortSettings(tag.getInt("controlMode"), tag.getBoolean("reverse"), tag.getInt("sortType"), tag.getInt("searchType"));
    }
}
