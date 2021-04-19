package com.hollingsworth.arsnouveau.common.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class SingleItemCapability {

    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IItemHandler.class, new Capability.IStorage<IItemHandler>() {
            @Override
            public INBT writeNBT(Capability<IItemHandler> capability, IItemHandler instance, Direction side) {
                ListNBT nbtTagList = new ListNBT();
                int size = instance.getSlots();
                for (int i = 0; i < size; i++) {
                    ItemStack stack = instance.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        CompoundNBT itemTag = new CompoundNBT();
                        itemTag.putInt("Slot", i);
                        stack.save(itemTag);
                        nbtTagList.add(itemTag);
                    }
                }
                return nbtTagList;
            }

            @Override
            public void readNBT(Capability<IItemHandler> capability, IItemHandler instance, Direction side, INBT base) {
                if (!(instance instanceof IItemHandlerModifiable))
                    throw new RuntimeException("IItemHandler instance does not implement IItemHandlerModifiable");
                IItemHandlerModifiable itemHandlerModifiable = (IItemHandlerModifiable) instance;
                ListNBT tagList = (ListNBT) base;
                for (int i = 0; i < tagList.size(); i++) {
                    CompoundNBT itemTags = tagList.getCompound(i);
                    int j = itemTags.getInt("Slot");

                    if (j >= 0 && j < instance.getSlots()) {
                        itemHandlerModifiable.setStackInSlot(j, ItemStack.of(itemTags));
                    }
                }
            }
        }, ItemStackHandler::new);
    }

}
