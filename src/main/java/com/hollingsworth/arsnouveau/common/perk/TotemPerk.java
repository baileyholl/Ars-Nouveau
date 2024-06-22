package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.nbt.PerkData;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class TotemPerk extends Perk {
    public static TotemPerk INSTANCE = new TotemPerk(ArsNouveau.prefix( "thread_undying"));

    public TotemPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public String getLangDescription() {
        return "Once every time you sleep, you will nullify death a single time as if holding a Totem of the Undying. Requires a tier 3 slot.";
    }

    @Override
    public PerkSlot minimumSlot() {
        return PerkSlot.THREE;
    }

    @Override
    public String getLangName() {
        return "Undying";
    }

    public static class Data extends PerkData{
        private boolean isActive;
        public Data(IPerkHolder<?> perkHolder) {
            super(perkHolder, INSTANCE);
            CompoundTag initTag = getInitTag();
            if(initTag != null) {
                // Set to true if the field is missing, because it's the first time to be used.
                this.isActive = !initTag.contains("isActive") || initTag.getBoolean("isActive");
            }
        }

        public void setActive(boolean active) {
            isActive = active;
            writePerks();
        }

        public boolean isActive(){
            return isActive;
        }

        @Override
        public void writeToNBT(CompoundTag tag) {
            super.writeToNBT(tag);
            tag.putBoolean("isActive", isActive);
        }
    }
}
