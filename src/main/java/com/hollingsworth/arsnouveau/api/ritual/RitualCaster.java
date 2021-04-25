package com.hollingsworth.arsnouveau.api.ritual;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.List;

public class RitualCaster implements IRitualCaster{

    List<String> ritualIDs = new ArrayList<>();
    String selectedRitualID = "";
    public ItemStack stack;

    private RitualCaster(ItemStack stack){
        this.stack = stack;
    }

    @Override
    public List<String> getUnlockedRitualIDs() {
        return ritualIDs;
    }

    @Override
    public void unlockRitual(String ritualID) {
        if(!ritualIDs.contains(ritualID))
            ritualIDs.add(ritualID);
        write(stack);
    }

    @Override
    public String getSelectedRitual() {
        return stack.getOrCreateTag().getString("selected");
    }

    @Override
    public void setRitual(AbstractRitual ritual) {
        setRitual(ritual.getID());
    }

    @Override
    public void setRitual(String ritualID) {
        stack.getOrCreateTag().putString("selected", ritualID);
        write(stack);
    }

    public static RitualCaster deserialize(ItemStack stack){
        RitualCaster instance = new RitualCaster(stack);
        CompoundNBT tag = stack.getOrCreateTag();
        ArrayList<String> rituals = new ArrayList<>();
        for(int i = 0; i < tag.getInt("numrituals"); i++){
            if(tag.contains("ritual_" + i))
                rituals.add(tag.getString("ritual_" + i));
        }
        instance.ritualIDs = rituals;
        instance.selectedRitualID = tag.getString("selected");
        return instance;
    }

    public void write(ItemStack stack){
        CompoundNBT tag = stack.getOrCreateTag();
        if(!this.selectedRitualID.isEmpty())
            tag.putString("selected", selectedRitualID);
        tag.putInt("numrituals", this.ritualIDs.size());
        for(int i = 0; i < ritualIDs.size(); i++){
            tag.putString("ritual_"+i, ritualIDs.get(i));
        }

        stack.setTag(tag);
    }
}
