package com.hollingsworth.craftedmagic.capability;

import com.hollingsworth.craftedmagic.api.mana.IMana;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class ManaStorage  implements Capability.IStorage<IMana> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<IMana> capability, IMana instance, Direction side) {
        return new IntNBT(instance.getCurrentMana());
    }

    @Override
    public void readNBT(Capability<IMana> capability, IMana instance, Direction side, INBT nbt) {
        instance.setMana(((IntNBT)nbt).getInt());
    }
}
