package com.hollingsworth.arsnouveau.api.nbt;

import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import net.minecraft.nbt.CompoundTag;

public abstract class PerkData extends AbstractData{
    public IPerkHolder<?> perkHolder;
    public IPerk perk;

    public PerkData(IPerkHolder<?> perkHolder, IPerk perk) {
        super(perkHolder.getTagForPerk(perk));
        this.perkHolder = perkHolder;
        this.perk = perk;
    }

    /**
     * Call this when the perk data is manipulated in order to save it to the PerkHolder.
     */
    public void writePerks(){
        CompoundTag tag = new CompoundTag();
        writeToNBT(tag);
        perkHolder.setTagForPerk(perk, tag);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {}
}
